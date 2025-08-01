import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IUtilisateur, NewUtilisateur } from '../utilisateur.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUtilisateur for edit and NewUtilisateurFormGroupInput for create.
 */
type UtilisateurFormGroupInput = IUtilisateur | PartialWithRequiredKeyOf<NewUtilisateur>;

type UtilisateurFormDefaults = Pick<NewUtilisateur, 'id' | 'isActive'>;

type UtilisateurFormGroupContent = {
  id: FormControl<IUtilisateur['id'] | NewUtilisateur['id']>;
  prenom: FormControl<IUtilisateur['prenom']>;
  nom: FormControl<IUtilisateur['nom']>;
  nomEntreprise: FormControl<IUtilisateur['nomEntreprise']>;
  secteurActivite: FormControl<IUtilisateur['secteurActivite']>;
  telephone: FormControl<IUtilisateur['telephone']>;
  role: FormControl<IUtilisateur['role']>;
  isActive: FormControl<IUtilisateur['isActive']>;
  user: FormControl<IUtilisateur['user']>;
  cv: FormControl<IUtilisateur['cv']>;
};

export type UtilisateurFormGroup = FormGroup<UtilisateurFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UtilisateurFormService {
  createUtilisateurFormGroup(utilisateur: UtilisateurFormGroupInput = { id: null }): UtilisateurFormGroup {
    const utilisateurRawValue = {
      ...this.getFormDefaults(),
      ...utilisateur,
    };
    return new FormGroup<UtilisateurFormGroupContent>({
      id: new FormControl(
        { value: utilisateurRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      prenom: new FormControl(utilisateurRawValue.prenom),
      nom: new FormControl(utilisateurRawValue.nom),
      nomEntreprise: new FormControl(utilisateurRawValue.nomEntreprise),
      secteurActivite: new FormControl(utilisateurRawValue.secteurActivite),
      telephone: new FormControl(utilisateurRawValue.telephone),
      role: new FormControl(utilisateurRawValue.role, {
        validators: [Validators.required],
      }),
      isActive: new FormControl(utilisateurRawValue.isActive, {
        validators: [Validators.required],
      }),
      user: new FormControl(utilisateurRawValue.user),
      cv: new FormControl(utilisateurRawValue.cv),
    });
  }

  getUtilisateur(form: UtilisateurFormGroup): IUtilisateur | NewUtilisateur {
    return form.getRawValue() as IUtilisateur | NewUtilisateur;
  }

  resetForm(form: UtilisateurFormGroup, utilisateur: UtilisateurFormGroupInput): void {
    const utilisateurRawValue = { ...this.getFormDefaults(), ...utilisateur };
    form.reset(
      {
        ...utilisateurRawValue,
        id: { value: utilisateurRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): UtilisateurFormDefaults {
    return {
      id: null,
      isActive: false,
    };
  }
}
