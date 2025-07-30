import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IOffreEmploi } from '../offre-emploi.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../offre-emploi.test-samples';

import { OffreEmploiService, RestOffreEmploi } from './offre-emploi.service';

const requireRestSample: RestOffreEmploi = {
  ...sampleWithRequiredData,
  datePublication: sampleWithRequiredData.datePublication?.toJSON(),
  dateExpiration: sampleWithRequiredData.dateExpiration?.toJSON(),
};

describe('OffreEmploi Service', () => {
  let service: OffreEmploiService;
  let httpMock: HttpTestingController;
  let expectedResult: IOffreEmploi | IOffreEmploi[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(OffreEmploiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a OffreEmploi', () => {
      const offreEmploi = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(offreEmploi).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OffreEmploi', () => {
      const offreEmploi = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(offreEmploi).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OffreEmploi', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OffreEmploi', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OffreEmploi', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOffreEmploiToCollectionIfMissing', () => {
      it('should add a OffreEmploi to an empty array', () => {
        const offreEmploi: IOffreEmploi = sampleWithRequiredData;
        expectedResult = service.addOffreEmploiToCollectionIfMissing([], offreEmploi);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(offreEmploi);
      });

      it('should not add a OffreEmploi to an array that contains it', () => {
        const offreEmploi: IOffreEmploi = sampleWithRequiredData;
        const offreEmploiCollection: IOffreEmploi[] = [
          {
            ...offreEmploi,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOffreEmploiToCollectionIfMissing(offreEmploiCollection, offreEmploi);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OffreEmploi to an array that doesn't contain it", () => {
        const offreEmploi: IOffreEmploi = sampleWithRequiredData;
        const offreEmploiCollection: IOffreEmploi[] = [sampleWithPartialData];
        expectedResult = service.addOffreEmploiToCollectionIfMissing(offreEmploiCollection, offreEmploi);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(offreEmploi);
      });

      it('should add only unique OffreEmploi to an array', () => {
        const offreEmploiArray: IOffreEmploi[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const offreEmploiCollection: IOffreEmploi[] = [sampleWithRequiredData];
        expectedResult = service.addOffreEmploiToCollectionIfMissing(offreEmploiCollection, ...offreEmploiArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const offreEmploi: IOffreEmploi = sampleWithRequiredData;
        const offreEmploi2: IOffreEmploi = sampleWithPartialData;
        expectedResult = service.addOffreEmploiToCollectionIfMissing([], offreEmploi, offreEmploi2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(offreEmploi);
        expect(expectedResult).toContain(offreEmploi2);
      });

      it('should accept null and undefined values', () => {
        const offreEmploi: IOffreEmploi = sampleWithRequiredData;
        expectedResult = service.addOffreEmploiToCollectionIfMissing([], null, offreEmploi, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(offreEmploi);
      });

      it('should return initial array if no OffreEmploi is added', () => {
        const offreEmploiCollection: IOffreEmploi[] = [sampleWithRequiredData];
        expectedResult = service.addOffreEmploiToCollectionIfMissing(offreEmploiCollection, undefined, null);
        expect(expectedResult).toEqual(offreEmploiCollection);
      });
    });

    describe('compareOffreEmploi', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOffreEmploi(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 12430 };
        const entity2 = null;

        const compareResult1 = service.compareOffreEmploi(entity1, entity2);
        const compareResult2 = service.compareOffreEmploi(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 12430 };
        const entity2 = { id: 17338 };

        const compareResult1 = service.compareOffreEmploi(entity1, entity2);
        const compareResult2 = service.compareOffreEmploi(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 12430 };
        const entity2 = { id: 12430 };

        const compareResult1 = service.compareOffreEmploi(entity1, entity2);
        const compareResult2 = service.compareOffreEmploi(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
