import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../transaction.test-samples';

import { TransactionFormService } from './transaction-form.service';

describe('Transaction Form Service', () => {
  let service: TransactionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TransactionFormService);
  });

  describe('Service methods', () => {
    describe('createTransactionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTransactionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            transactionDate: expect.any(Object),
            senderNumber: expect.any(Object),
            senderWallet: expect.any(Object),
            receiverNumber: expect.any(Object),
            receiverWallet: expect.any(Object),
            transactionStatus: expect.any(Object),
            payInStatus: expect.any(Object),
            payOutStatus: expect.any(Object),
            amount: expect.any(Object),
            object: expect.any(Object),
            payInFailureReason: expect.any(Object),
            payOutFailureReason: expect.any(Object),
            senderCountryName: expect.any(Object),
            receiverCountryName: expect.any(Object),
            customer: expect.any(Object),
          }),
        );
      });

      it('passing ITransaction should create a new form with FormGroup', () => {
        const formGroup = service.createTransactionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            transactionDate: expect.any(Object),
            senderNumber: expect.any(Object),
            senderWallet: expect.any(Object),
            receiverNumber: expect.any(Object),
            receiverWallet: expect.any(Object),
            transactionStatus: expect.any(Object),
            payInStatus: expect.any(Object),
            payOutStatus: expect.any(Object),
            amount: expect.any(Object),
            object: expect.any(Object),
            payInFailureReason: expect.any(Object),
            payOutFailureReason: expect.any(Object),
            senderCountryName: expect.any(Object),
            receiverCountryName: expect.any(Object),
            customer: expect.any(Object),
          }),
        );
      });
    });

    describe('getTransaction', () => {
      it('should return NewTransaction for default Transaction initial value', () => {
        const formGroup = service.createTransactionFormGroup(sampleWithNewData);

        const transaction = service.getTransaction(formGroup) as any;

        expect(transaction).toMatchObject(sampleWithNewData);
      });

      it('should return NewTransaction for empty Transaction initial value', () => {
        const formGroup = service.createTransactionFormGroup();

        const transaction = service.getTransaction(formGroup) as any;

        expect(transaction).toMatchObject({});
      });

      it('should return ITransaction', () => {
        const formGroup = service.createTransactionFormGroup(sampleWithRequiredData);

        const transaction = service.getTransaction(formGroup) as any;

        expect(transaction).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITransaction should not enable id FormControl', () => {
        const formGroup = service.createTransactionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTransaction should disable id FormControl', () => {
        const formGroup = service.createTransactionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
