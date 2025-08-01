import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../offre-emploi.test-samples';

import { OffreEmploiFormService } from './offre-emploi-form.service';

describe('OffreEmploi Form Service', () => {
  let service: OffreEmploiFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OffreEmploiFormService);
  });

  describe('Service methods', () => {
    describe('createOffreEmploiFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOffreEmploiFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titre: expect.any(Object),
            description: expect.any(Object),
            localisation: expect.any(Object),
            salaire: expect.any(Object),
            datePublication: expect.any(Object),
            dateExpiration: expect.any(Object),
            isActive: expect.any(Object),
            typeContrat: expect.any(Object),
            recruteur: expect.any(Object),
          }),
        );
      });

      it('passing IOffreEmploi should create a new form with FormGroup', () => {
        const formGroup = service.createOffreEmploiFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titre: expect.any(Object),
            description: expect.any(Object),
            localisation: expect.any(Object),
            salaire: expect.any(Object),
            datePublication: expect.any(Object),
            dateExpiration: expect.any(Object),
            isActive: expect.any(Object),
            typeContrat: expect.any(Object),
            recruteur: expect.any(Object),
          }),
        );
      });
    });

    describe('getOffreEmploi', () => {
      it('should return NewOffreEmploi for default OffreEmploi initial value', () => {
        const formGroup = service.createOffreEmploiFormGroup(sampleWithNewData);

        const offreEmploi = service.getOffreEmploi(formGroup) as any;

        expect(offreEmploi).toMatchObject(sampleWithNewData);
      });

      it('should return NewOffreEmploi for empty OffreEmploi initial value', () => {
        const formGroup = service.createOffreEmploiFormGroup();

        const offreEmploi = service.getOffreEmploi(formGroup) as any;

        expect(offreEmploi).toMatchObject({});
      });

      it('should return IOffreEmploi', () => {
        const formGroup = service.createOffreEmploiFormGroup(sampleWithRequiredData);

        const offreEmploi = service.getOffreEmploi(formGroup) as any;

        expect(offreEmploi).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOffreEmploi should not enable id FormControl', () => {
        const formGroup = service.createOffreEmploiFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOffreEmploi should disable id FormControl', () => {
        const formGroup = service.createOffreEmploiFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
