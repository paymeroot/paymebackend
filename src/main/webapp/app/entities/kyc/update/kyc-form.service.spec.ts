import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../kyc.test-samples';

import { KycFormService } from './kyc-form.service';

describe('Kyc Form Service', () => {
  let service: KycFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(KycFormService);
  });

  describe('Service methods', () => {
    describe('createKycFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createKycFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            typePiece: expect.any(Object),
            numberPiece: expect.any(Object),
            photoPieceUrl: expect.any(Object),
            photoSelfieUrl: expect.any(Object),
          }),
        );
      });

      it('passing IKyc should create a new form with FormGroup', () => {
        const formGroup = service.createKycFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            typePiece: expect.any(Object),
            numberPiece: expect.any(Object),
            photoPieceUrl: expect.any(Object),
            photoSelfieUrl: expect.any(Object),
          }),
        );
      });
    });

    describe('getKyc', () => {
      it('should return NewKyc for default Kyc initial value', () => {
        const formGroup = service.createKycFormGroup(sampleWithNewData);

        const kyc = service.getKyc(formGroup) as any;

        expect(kyc).toMatchObject(sampleWithNewData);
      });

      it('should return NewKyc for empty Kyc initial value', () => {
        const formGroup = service.createKycFormGroup();

        const kyc = service.getKyc(formGroup) as any;

        expect(kyc).toMatchObject({});
      });

      it('should return IKyc', () => {
        const formGroup = service.createKycFormGroup(sampleWithRequiredData);

        const kyc = service.getKyc(formGroup) as any;

        expect(kyc).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IKyc should not enable id FormControl', () => {
        const formGroup = service.createKycFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewKyc should disable id FormControl', () => {
        const formGroup = service.createKycFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
