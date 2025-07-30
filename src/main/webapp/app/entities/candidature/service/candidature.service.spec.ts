import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICandidature } from '../candidature.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../candidature.test-samples';

import { CandidatureService, RestCandidature } from './candidature.service';

const requireRestSample: RestCandidature = {
  ...sampleWithRequiredData,
  datePostulation: sampleWithRequiredData.datePostulation?.toJSON(),
};

describe('Candidature Service', () => {
  let service: CandidatureService;
  let httpMock: HttpTestingController;
  let expectedResult: ICandidature | ICandidature[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CandidatureService);
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

    it('should create a Candidature', () => {
      const candidature = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(candidature).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Candidature', () => {
      const candidature = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(candidature).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Candidature', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Candidature', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Candidature', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCandidatureToCollectionIfMissing', () => {
      it('should add a Candidature to an empty array', () => {
        const candidature: ICandidature = sampleWithRequiredData;
        expectedResult = service.addCandidatureToCollectionIfMissing([], candidature);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(candidature);
      });

      it('should not add a Candidature to an array that contains it', () => {
        const candidature: ICandidature = sampleWithRequiredData;
        const candidatureCollection: ICandidature[] = [
          {
            ...candidature,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCandidatureToCollectionIfMissing(candidatureCollection, candidature);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Candidature to an array that doesn't contain it", () => {
        const candidature: ICandidature = sampleWithRequiredData;
        const candidatureCollection: ICandidature[] = [sampleWithPartialData];
        expectedResult = service.addCandidatureToCollectionIfMissing(candidatureCollection, candidature);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(candidature);
      });

      it('should add only unique Candidature to an array', () => {
        const candidatureArray: ICandidature[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const candidatureCollection: ICandidature[] = [sampleWithRequiredData];
        expectedResult = service.addCandidatureToCollectionIfMissing(candidatureCollection, ...candidatureArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const candidature: ICandidature = sampleWithRequiredData;
        const candidature2: ICandidature = sampleWithPartialData;
        expectedResult = service.addCandidatureToCollectionIfMissing([], candidature, candidature2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(candidature);
        expect(expectedResult).toContain(candidature2);
      });

      it('should accept null and undefined values', () => {
        const candidature: ICandidature = sampleWithRequiredData;
        expectedResult = service.addCandidatureToCollectionIfMissing([], null, candidature, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(candidature);
      });

      it('should return initial array if no Candidature is added', () => {
        const candidatureCollection: ICandidature[] = [sampleWithRequiredData];
        expectedResult = service.addCandidatureToCollectionIfMissing(candidatureCollection, undefined, null);
        expect(expectedResult).toEqual(candidatureCollection);
      });
    });

    describe('compareCandidature', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCandidature(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 17844 };
        const entity2 = null;

        const compareResult1 = service.compareCandidature(entity1, entity2);
        const compareResult2 = service.compareCandidature(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 17844 };
        const entity2 = { id: 12242 };

        const compareResult1 = service.compareCandidature(entity1, entity2);
        const compareResult2 = service.compareCandidature(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 17844 };
        const entity2 = { id: 17844 };

        const compareResult1 = service.compareCandidature(entity1, entity2);
        const compareResult2 = service.compareCandidature(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
