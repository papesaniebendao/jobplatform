import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICV, NewCV } from '../cv.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICV for edit and NewCVFormGroupInput for create.
 */
type CVFormGroupInput = ICV | PartialWithRequiredKeyOf<NewCV>;

type CVFormDefaults = Pick<NewCV, 'id'>;

type CVFormGroupContent = {
  id: FormControl<ICV['id'] | NewCV['id']>;
  urlFichier: FormControl<ICV['urlFichier']>;
};

export type CVFormGroup = FormGroup<CVFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CVFormService {
  createCVFormGroup(cV: CVFormGroupInput = { id: null }): CVFormGroup {
    const cVRawValue = {
      ...this.getFormDefaults(),
      ...cV,
    };
    return new FormGroup<CVFormGroupContent>({
      id: new FormControl(
        { value: cVRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      urlFichier: new FormControl(cVRawValue.urlFichier, {
        validators: [Validators.required],
      }),
    });
  }

  getCV(form: CVFormGroup): ICV | NewCV {
    return form.getRawValue() as ICV | NewCV;
  }

  resetForm(form: CVFormGroup, cV: CVFormGroupInput): void {
    const cVRawValue = { ...this.getFormDefaults(), ...cV };
    form.reset(
      {
        ...cVRawValue,
        id: { value: cVRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CVFormDefaults {
    return {
      id: null,
    };
  }
}
