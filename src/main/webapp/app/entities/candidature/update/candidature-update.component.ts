import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUtilisateur } from 'app/entities/utilisateur/utilisateur.model';
import { UtilisateurService } from 'app/entities/utilisateur/service/utilisateur.service';
import { IOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';
import { OffreEmploiService } from 'app/entities/offre-emploi/service/offre-emploi.service';
import { StatutCandidature } from 'app/entities/enumerations/statut-candidature.model';
import { CandidatureService } from '../service/candidature.service';
import { ICandidature } from '../candidature.model';
import { CandidatureFormGroup, CandidatureFormService } from './candidature-form.service';

@Component({
  selector: 'jhi-candidature-update',
  templateUrl: './candidature-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CandidatureUpdateComponent implements OnInit {
  isSaving = false;
  candidature: ICandidature | null = null;
  statutCandidatureValues = Object.keys(StatutCandidature);

  utilisateursSharedCollection: IUtilisateur[] = [];
  offreEmploisSharedCollection: IOffreEmploi[] = [];

  protected candidatureService = inject(CandidatureService);
  protected candidatureFormService = inject(CandidatureFormService);
  protected utilisateurService = inject(UtilisateurService);
  protected offreEmploiService = inject(OffreEmploiService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CandidatureFormGroup = this.candidatureFormService.createCandidatureFormGroup();

  compareUtilisateur = (o1: IUtilisateur | null, o2: IUtilisateur | null): boolean => this.utilisateurService.compareUtilisateur(o1, o2);

  compareOffreEmploi = (o1: IOffreEmploi | null, o2: IOffreEmploi | null): boolean => this.offreEmploiService.compareOffreEmploi(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ candidature }) => {
      this.candidature = candidature;
      if (candidature) {
        this.updateForm(candidature);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const candidature = this.candidatureFormService.getCandidature(this.editForm);
    if (candidature.id !== null) {
      this.subscribeToSaveResponse(this.candidatureService.update(candidature));
    } else {
      this.subscribeToSaveResponse(this.candidatureService.create(candidature));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICandidature>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(candidature: ICandidature): void {
    this.candidature = candidature;
    this.candidatureFormService.resetForm(this.editForm, candidature);

    this.utilisateursSharedCollection = this.utilisateurService.addUtilisateurToCollectionIfMissing<IUtilisateur>(
      this.utilisateursSharedCollection,
      candidature.candidat,
    );
    this.offreEmploisSharedCollection = this.offreEmploiService.addOffreEmploiToCollectionIfMissing<IOffreEmploi>(
      this.offreEmploisSharedCollection,
      candidature.offreEmploi,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.utilisateurService
      .query()
      .pipe(map((res: HttpResponse<IUtilisateur[]>) => res.body ?? []))
      .pipe(
        map((utilisateurs: IUtilisateur[]) =>
          this.utilisateurService.addUtilisateurToCollectionIfMissing<IUtilisateur>(utilisateurs, this.candidature?.candidat),
        ),
      )
      .subscribe((utilisateurs: IUtilisateur[]) => (this.utilisateursSharedCollection = utilisateurs));

    this.offreEmploiService
      .query()
      .pipe(map((res: HttpResponse<IOffreEmploi[]>) => res.body ?? []))
      .pipe(
        map((offreEmplois: IOffreEmploi[]) =>
          this.offreEmploiService.addOffreEmploiToCollectionIfMissing<IOffreEmploi>(offreEmplois, this.candidature?.offreEmploi),
        ),
      )
      .subscribe((offreEmplois: IOffreEmploi[]) => (this.offreEmploisSharedCollection = offreEmplois));
  }
}
