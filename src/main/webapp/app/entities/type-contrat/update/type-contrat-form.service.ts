import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITypeContrat, NewTypeContrat } from '../type-contrat.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITypeContrat for edit and NewTypeContratFormGroupInput for create.
 */
type TypeContratFormGroupInput = ITypeContrat | PartialWithRequiredKeyOf<NewTypeContrat>;

type TypeContratFormDefaults = Pick<NewTypeContrat, 'id'>;

type TypeContratFormGroupContent = {
  id: FormControl<ITypeContrat['id'] | NewTypeContrat['id']>;
  nom: FormControl<ITypeContrat['nom']>;
};

export type TypeContratFormGroup = FormGroup<TypeContratFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TypeContratFormService {
  createTypeContratFormGroup(typeContrat: TypeContratFormGroupInput = { id: null }): TypeContratFormGroup {
    const typeContratRawValue = {
      ...this.getFormDefaults(),
      ...typeContrat,
    };
    return new FormGroup<TypeContratFormGroupContent>({
      id: new FormControl(
        { value: typeContratRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(typeContratRawValue.nom, {
        validators: [Validators.required],
      }),
    });
  }

  getTypeContrat(form: TypeContratFormGroup): ITypeContrat | NewTypeContrat {
    return form.getRawValue() as ITypeContrat | NewTypeContrat;
  }

  resetForm(form: TypeContratFormGroup, typeContrat: TypeContratFormGroupInput): void {
    const typeContratRawValue = { ...this.getFormDefaults(), ...typeContrat };
    form.reset(
      {
        ...typeContratRawValue,
        id: { value: typeContratRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TypeContratFormDefaults {
    return {
      id: null,
    };
  }
}
