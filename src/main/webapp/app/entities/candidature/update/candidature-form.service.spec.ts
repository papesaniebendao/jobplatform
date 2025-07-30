import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../candidature.test-samples';

import { CandidatureFormService } from './candidature-form.service';

describe('Candidature Form Service', () => {
  let service: CandidatureFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CandidatureFormService);
  });

  describe('Service methods', () => {
    describe('createCandidatureFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCandidatureFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            datePostulation: expect.any(Object),
            statut: expect.any(Object),
            candidat: expect.any(Object),
            offreEmploi: expect.any(Object),
          }),
        );
      });

      it('passing ICandidature should create a new form with FormGroup', () => {
        const formGroup = service.createCandidatureFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            datePostulation: expect.any(Object),
            statut: expect.any(Object),
            candidat: expect.any(Object),
            offreEmploi: expect.any(Object),
          }),
        );
      });
    });

    describe('getCandidature', () => {
      it('should return NewCandidature for default Candidature initial value', () => {
        const formGroup = service.createCandidatureFormGroup(sampleWithNewData);

        const candidature = service.getCandidature(formGroup) as any;

        expect(candidature).toMatchObject(sampleWithNewData);
      });

      it('should return NewCandidature for empty Candidature initial value', () => {
        const formGroup = service.createCandidatureFormGroup();

        const candidature = service.getCandidature(formGroup) as any;

        expect(candidature).toMatchObject({});
      });

      it('should return ICandidature', () => {
        const formGroup = service.createCandidatureFormGroup(sampleWithRequiredData);

        const candidature = service.getCandidature(formGroup) as any;

        expect(candidature).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICandidature should not enable id FormControl', () => {
        const formGroup = service.createCandidatureFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCandidature should disable id FormControl', () => {
        const formGroup = service.createCandidatureFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
