import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ICV } from 'app/entities/cv/cv.model';
import { CVService } from 'app/entities/cv/service/cv.service';
import { RoleUtilisateur } from 'app/entities/enumerations/role-utilisateur.model';
import { UtilisateurService } from '../service/utilisateur.service';
import { IUtilisateur } from '../utilisateur.model';
import { UtilisateurFormGroup, UtilisateurFormService } from './utilisateur-form.service';

@Component({
  selector: 'jhi-utilisateur-update',
  templateUrl: './utilisateur-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class UtilisateurUpdateComponent implements OnInit {
  isSaving = false;
  utilisateur: IUtilisateur | null = null;
  roleUtilisateurValues = Object.keys(RoleUtilisateur);

  usersSharedCollection: IUser[] = [];
  cvsCollection: ICV[] = [];

  protected utilisateurService = inject(UtilisateurService);
  protected utilisateurFormService = inject(UtilisateurFormService);
  protected userService = inject(UserService);
  protected cVService = inject(CVService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UtilisateurFormGroup = this.utilisateurFormService.createUtilisateurFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareCV = (o1: ICV | null, o2: ICV | null): boolean => this.cVService.compareCV(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ utilisateur }) => {
      this.utilisateur = utilisateur;
      if (utilisateur) {
        this.updateForm(utilisateur);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const utilisateur = this.utilisateurFormService.getUtilisateur(this.editForm);
    if (utilisateur.id !== null) {
      this.subscribeToSaveResponse(this.utilisateurService.update(utilisateur));
    } else {
      this.subscribeToSaveResponse(this.utilisateurService.create(utilisateur));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUtilisateur>>): void {
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

  protected updateForm(utilisateur: IUtilisateur): void {
    this.utilisateur = utilisateur;
    this.utilisateurFormService.resetForm(this.editForm, utilisateur);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, utilisateur.user);
    this.cvsCollection = this.cVService.addCVToCollectionIfMissing<ICV>(this.cvsCollection, utilisateur.cv);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.utilisateur?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.cVService
      .query({ filter: 'utilisateur-is-null' })
      .pipe(map((res: HttpResponse<ICV[]>) => res.body ?? []))
      .pipe(map((cVS: ICV[]) => this.cVService.addCVToCollectionIfMissing<ICV>(cVS, this.utilisateur?.cv)))
      .subscribe((cVS: ICV[]) => (this.cvsCollection = cVS));
  }
}
