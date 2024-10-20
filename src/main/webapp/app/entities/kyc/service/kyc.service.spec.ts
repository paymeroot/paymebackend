import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IKyc } from '../kyc.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../kyc.test-samples';

import { KycService } from './kyc.service';

const requireRestSample: IKyc = {
  ...sampleWithRequiredData,
};

describe('Kyc Service', () => {
  let service: KycService;
  let httpMock: HttpTestingController;
  let expectedResult: IKyc | IKyc[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(KycService);
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

    it('should create a Kyc', () => {
      const kyc = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(kyc).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Kyc', () => {
      const kyc = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(kyc).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Kyc', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Kyc', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Kyc', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Kyc', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addKycToCollectionIfMissing', () => {
      it('should add a Kyc to an empty array', () => {
        const kyc: IKyc = sampleWithRequiredData;
        expectedResult = service.addKycToCollectionIfMissing([], kyc);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(kyc);
      });

      it('should not add a Kyc to an array that contains it', () => {
        const kyc: IKyc = sampleWithRequiredData;
        const kycCollection: IKyc[] = [
          {
            ...kyc,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addKycToCollectionIfMissing(kycCollection, kyc);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Kyc to an array that doesn't contain it", () => {
        const kyc: IKyc = sampleWithRequiredData;
        const kycCollection: IKyc[] = [sampleWithPartialData];
        expectedResult = service.addKycToCollectionIfMissing(kycCollection, kyc);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(kyc);
      });

      it('should add only unique Kyc to an array', () => {
        const kycArray: IKyc[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const kycCollection: IKyc[] = [sampleWithRequiredData];
        expectedResult = service.addKycToCollectionIfMissing(kycCollection, ...kycArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const kyc: IKyc = sampleWithRequiredData;
        const kyc2: IKyc = sampleWithPartialData;
        expectedResult = service.addKycToCollectionIfMissing([], kyc, kyc2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(kyc);
        expect(expectedResult).toContain(kyc2);
      });

      it('should accept null and undefined values', () => {
        const kyc: IKyc = sampleWithRequiredData;
        expectedResult = service.addKycToCollectionIfMissing([], null, kyc, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(kyc);
      });

      it('should return initial array if no Kyc is added', () => {
        const kycCollection: IKyc[] = [sampleWithRequiredData];
        expectedResult = service.addKycToCollectionIfMissing(kycCollection, undefined, null);
        expect(expectedResult).toEqual(kycCollection);
      });
    });

    describe('compareKyc', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareKyc(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareKyc(entity1, entity2);
        const compareResult2 = service.compareKyc(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareKyc(entity1, entity2);
        const compareResult2 = service.compareKyc(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareKyc(entity1, entity2);
        const compareResult2 = service.compareKyc(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
