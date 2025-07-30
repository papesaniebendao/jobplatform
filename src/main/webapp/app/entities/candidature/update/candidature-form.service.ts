import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICandidature, NewCandidature } from '../candidature.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICandidature for edit and NewCandidatureFormGroupInput for create.
 */
type CandidatureFormGroupInput = ICandidature | PartialWithRequiredKeyOf<NewCandidature>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICandidature | NewCandidature> = Omit<T, 'datePostulation'> & {
  datePostulation?: string | null;
};

type CandidatureFormRawValue = FormValueOf<ICandidature>;

type NewCandidatureFormRawValue = FormValueOf<NewCandidature>;

type CandidatureFormDefaults = Pick<NewCandidature, 'id' | 'datePostulation'>;

type CandidatureFormGroupContent = {
  id: FormControl<CandidatureFormRawValue['id'] | NewCandidature['id']>;
  datePostulation: FormControl<CandidatureFormRawValue['datePostulation']>;
  statut: FormControl<CandidatureFormRawValue['statut']>;
  candidat: FormControl<CandidatureFormRawValue['candidat']>;
  offreEmploi: FormControl<CandidatureFormRawValue['offreEmploi']>;
};

export type CandidatureFormGroup = FormGroup<CandidatureFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CandidatureFormService {
  createCandidatureFormGroup(candidature: CandidatureFormGroupInput = { id: null }): CandidatureFormGroup {
    const candidatureRawValue = this.convertCandidatureToCandidatureRawValue({
      ...this.getFormDefaults(),
      ...candidature,
    });
    return new FormGroup<CandidatureFormGroupContent>({
      id: new FormControl(
        { value: candidatureRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      datePostulation: new FormControl(candidatureRawValue.datePostulation, {
        validators: [Validators.required],
      }),
      statut: new FormControl(candidatureRawValue.statut, {
        validators: [Validators.required],
      }),
      candidat: new FormControl(candidatureRawValue.candidat),
      offreEmploi: new FormControl(candidatureRawValue.offreEmploi),
    });
  }

  getCandidature(form: CandidatureFormGroup): ICandidature | NewCandidature {
    return this.convertCandidatureRawValueToCandidature(form.getRawValue() as CandidatureFormRawValue | NewCandidatureFormRawValue);
  }

  resetForm(form: CandidatureFormGroup, candidature: CandidatureFormGroupInput): void {
    const candidatureRawValue = this.convertCandidatureToCandidatureRawValue({ ...this.getFormDefaults(), ...candidature });
    form.reset(
      {
        ...candidatureRawValue,
        id: { value: candidatureRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CandidatureFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      datePostulation: currentTime,
    };
  }

  private convertCandidatureRawValueToCandidature(
    rawCandidature: CandidatureFormRawValue | NewCandidatureFormRawValue,
  ): ICandidature | NewCandidature {
    return {
      ...rawCandidature,
      datePostulation: dayjs(rawCandidature.datePostulation, DATE_TIME_FORMAT),
    };
  }

  private convertCandidatureToCandidatureRawValue(
    candidature: ICandidature | (Partial<NewCandidature> & CandidatureFormDefaults),
  ): CandidatureFormRawValue | PartialWithRequiredKeyOf<NewCandidatureFormRawValue> {
    return {
      ...candidature,
      datePostulation: candidature.datePostulation ? candidature.datePostulation.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
