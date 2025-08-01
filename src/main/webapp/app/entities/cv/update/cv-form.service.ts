import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
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

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICV | NewCV> = Omit<T, 'dateUpload'> & {
  dateUpload?: string | null;
};

type CVFormRawValue = FormValueOf<ICV>;

type NewCVFormRawValue = FormValueOf<NewCV>;

type CVFormDefaults = Pick<NewCV, 'id' | 'dateUpload'>;

type CVFormGroupContent = {
  id: FormControl<CVFormRawValue['id'] | NewCV['id']>;
  urlFichier: FormControl<CVFormRawValue['urlFichier']>;
  nomFichier: FormControl<CVFormRawValue['nomFichier']>;
  dateUpload: FormControl<CVFormRawValue['dateUpload']>;
};

export type CVFormGroup = FormGroup<CVFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CVFormService {
  createCVFormGroup(cV: CVFormGroupInput = { id: null }): CVFormGroup {
    const cVRawValue = this.convertCVToCVRawValue({
      ...this.getFormDefaults(),
      ...cV,
    });
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
      nomFichier: new FormControl(cVRawValue.nomFichier),
      dateUpload: new FormControl(cVRawValue.dateUpload),
    });
  }

  getCV(form: CVFormGroup): ICV | NewCV {
    return this.convertCVRawValueToCV(form.getRawValue() as CVFormRawValue | NewCVFormRawValue);
  }

  resetForm(form: CVFormGroup, cV: CVFormGroupInput): void {
    const cVRawValue = this.convertCVToCVRawValue({ ...this.getFormDefaults(), ...cV });
    form.reset(
      {
        ...cVRawValue,
        id: { value: cVRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CVFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateUpload: currentTime,
    };
  }

  private convertCVRawValueToCV(rawCV: CVFormRawValue | NewCVFormRawValue): ICV | NewCV {
    return {
      ...rawCV,
      dateUpload: dayjs(rawCV.dateUpload, DATE_TIME_FORMAT),
    };
  }

  private convertCVToCVRawValue(cV: ICV | (Partial<NewCV> & CVFormDefaults)): CVFormRawValue | PartialWithRequiredKeyOf<NewCVFormRawValue> {
    return {
      ...cV,
      dateUpload: cV.dateUpload ? cV.dateUpload.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
