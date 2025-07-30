import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITypeContrat } from '../type-contrat.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../type-contrat.test-samples';

import { TypeContratService } from './type-contrat.service';

const requireRestSample: ITypeContrat = {
  ...sampleWithRequiredData,
};

describe('TypeContrat Service', () => {
  let service: TypeContratService;
  let httpMock: HttpTestingController;
  let expectedResult: ITypeContrat | ITypeContrat[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TypeContratService);
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

    it('should create a TypeContrat', () => {
      const typeContrat = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(typeContrat).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TypeContrat', () => {
      const typeContrat = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(typeContrat).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TypeContrat', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TypeContrat', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TypeContrat', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTypeContratToCollectionIfMissing', () => {
      it('should add a TypeContrat to an empty array', () => {
        const typeContrat: ITypeContrat = sampleWithRequiredData;
        expectedResult = service.addTypeContratToCollectionIfMissing([], typeContrat);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(typeContrat);
      });

      it('should not add a TypeContrat to an array that contains it', () => {
        const typeContrat: ITypeContrat = sampleWithRequiredData;
        const typeContratCollection: ITypeContrat[] = [
          {
            ...typeContrat,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTypeContratToCollectionIfMissing(typeContratCollection, typeContrat);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TypeContrat to an array that doesn't contain it", () => {
        const typeContrat: ITypeContrat = sampleWithRequiredData;
        const typeContratCollection: ITypeContrat[] = [sampleWithPartialData];
        expectedResult = service.addTypeContratToCollectionIfMissing(typeContratCollection, typeContrat);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(typeContrat);
      });

      it('should add only unique TypeContrat to an array', () => {
        const typeContratArray: ITypeContrat[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const typeContratCollection: ITypeContrat[] = [sampleWithRequiredData];
        expectedResult = service.addTypeContratToCollectionIfMissing(typeContratCollection, ...typeContratArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const typeContrat: ITypeContrat = sampleWithRequiredData;
        const typeContrat2: ITypeContrat = sampleWithPartialData;
        expectedResult = service.addTypeContratToCollectionIfMissing([], typeContrat, typeContrat2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(typeContrat);
        expect(expectedResult).toContain(typeContrat2);
      });

      it('should accept null and undefined values', () => {
        const typeContrat: ITypeContrat = sampleWithRequiredData;
        expectedResult = service.addTypeContratToCollectionIfMissing([], null, typeContrat, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(typeContrat);
      });

      it('should return initial array if no TypeContrat is added', () => {
        const typeContratCollection: ITypeContrat[] = [sampleWithRequiredData];
        expectedResult = service.addTypeContratToCollectionIfMissing(typeContratCollection, undefined, null);
        expect(expectedResult).toEqual(typeContratCollection);
      });
    });

    describe('compareTypeContrat', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTypeContrat(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 13691 };
        const entity2 = null;

        const compareResult1 = service.compareTypeContrat(entity1, entity2);
        const compareResult2 = service.compareTypeContrat(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 13691 };
        const entity2 = { id: 16761 };

        const compareResult1 = service.compareTypeContrat(entity1, entity2);
        const compareResult2 = service.compareTypeContrat(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 13691 };
        const entity2 = { id: 13691 };

        const compareResult1 = service.compareTypeContrat(entity1, entity2);
        const compareResult2 = service.compareTypeContrat(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
