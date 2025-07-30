import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITypeContrat } from 'app/entities/type-contrat/type-contrat.model';
import { TypeContratService } from 'app/entities/type-contrat/service/type-contrat.service';
import { IUtilisateur } from 'app/entities/utilisateur/utilisateur.model';
import { UtilisateurService } from 'app/entities/utilisateur/service/utilisateur.service';
import { OffreEmploiService } from '../service/offre-emploi.service';
import { IOffreEmploi } from '../offre-emploi.model';
import { OffreEmploiFormGroup, OffreEmploiFormService } from './offre-emploi-form.service';

@Component({
  selector: 'jhi-offre-emploi-update',
  templateUrl: './offre-emploi-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class OffreEmploiUpdateComponent implements OnInit {
  isSaving = false;
  offreEmploi: IOffreEmploi | null = null;

  typeContratsSharedCollection: ITypeContrat[] = [];
  utilisateursSharedCollection: IUtilisateur[] = [];

  protected offreEmploiService = inject(OffreEmploiService);
  protected offreEmploiFormService = inject(OffreEmploiFormService);
  protected typeContratService = inject(TypeContratService);
  protected utilisateurService = inject(UtilisateurService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: OffreEmploiFormGroup = this.offreEmploiFormService.createOffreEmploiFormGroup();

  compareTypeContrat = (o1: ITypeContrat | null, o2: ITypeContrat | null): boolean => this.typeContratService.compareTypeContrat(o1, o2);

  compareUtilisateur = (o1: IUtilisateur | null, o2: IUtilisateur | null): boolean => this.utilisateurService.compareUtilisateur(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ offreEmploi }) => {
      this.offreEmploi = offreEmploi;
      if (offreEmploi) {
        this.updateForm(offreEmploi);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const offreEmploi = this.offreEmploiFormService.getOffreEmploi(this.editForm);
    if (offreEmploi.id !== null) {
      this.subscribeToSaveResponse(this.offreEmploiService.update(offreEmploi));
    } else {
      this.subscribeToSaveResponse(this.offreEmploiService.create(offreEmploi));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOffreEmploi>>): void {
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

  protected updateForm(offreEmploi: IOffreEmploi): void {
    this.offreEmploi = offreEmploi;
    this.offreEmploiFormService.resetForm(this.editForm, offreEmploi);

    this.typeContratsSharedCollection = this.typeContratService.addTypeContratToCollectionIfMissing<ITypeContrat>(
      this.typeContratsSharedCollection,
      offreEmploi.typeContrat,
    );
    this.utilisateursSharedCollection = this.utilisateurService.addUtilisateurToCollectionIfMissing<IUtilisateur>(
      this.utilisateursSharedCollection,
      offreEmploi.recruteur,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.typeContratService
      .query()
      .pipe(map((res: HttpResponse<ITypeContrat[]>) => res.body ?? []))
      .pipe(
        map((typeContrats: ITypeContrat[]) =>
          this.typeContratService.addTypeContratToCollectionIfMissing<ITypeContrat>(typeContrats, this.offreEmploi?.typeContrat),
        ),
      )
      .subscribe((typeContrats: ITypeContrat[]) => (this.typeContratsSharedCollection = typeContrats));

    this.utilisateurService
      .query()
      .pipe(map((res: HttpResponse<IUtilisateur[]>) => res.body ?? []))
      .pipe(
        map((utilisateurs: IUtilisateur[]) =>
          this.utilisateurService.addUtilisateurToCollectionIfMissing<IUtilisateur>(utilisateurs, this.offreEmploi?.recruteur),
        ),
      )
      .subscribe((utilisateurs: IUtilisateur[]) => (this.utilisateursSharedCollection = utilisateurs));
  }
}
