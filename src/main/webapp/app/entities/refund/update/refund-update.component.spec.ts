import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ITransaction } from 'app/entities/transaction/transaction.model';
import { TransactionService } from 'app/entities/transaction/service/transaction.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { IRefund } from '../refund.model';
import { RefundService } from '../service/refund.service';
import { RefundFormService } from './refund-form.service';

import { RefundUpdateComponent } from './refund-update.component';

describe('Refund Management Update Component', () => {
  let comp: RefundUpdateComponent;
  let fixture: ComponentFixture<RefundUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let refundFormService: RefundFormService;
  let refundService: RefundService;
  let transactionService: TransactionService;
  let customerService: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RefundUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(RefundUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RefundUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    refundFormService = TestBed.inject(RefundFormService);
    refundService = TestBed.inject(RefundService);
    transactionService = TestBed.inject(TransactionService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call transaction query and add missing value', () => {
      const refund: IRefund = { id: 456 };
      const transaction: ITransaction = { id: 10348 };
      refund.transaction = transaction;

      const transactionCollection: ITransaction[] = [{ id: 24398 }];
      jest.spyOn(transactionService, 'query').mockReturnValue(of(new HttpResponse({ body: transactionCollection })));
      const expectedCollection: ITransaction[] = [transaction, ...transactionCollection];
      jest.spyOn(transactionService, 'addTransactionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ refund });
      comp.ngOnInit();

      expect(transactionService.query).toHaveBeenCalled();
      expect(transactionService.addTransactionToCollectionIfMissing).toHaveBeenCalledWith(transactionCollection, transaction);
      expect(comp.transactionsCollection).toEqual(expectedCollection);
    });

    it('Should call Customer query and add missing value', () => {
      const refund: IRefund = { id: 456 };
      const customer: ICustomer = { id: 10891 };
      refund.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 15247 }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ refund });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(expect.objectContaining),
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const refund: IRefund = { id: 456 };
      const transaction: ITransaction = { id: 25558 };
      refund.transaction = transaction;
      const customer: ICustomer = { id: 22885 };
      refund.customer = customer;

      activatedRoute.data = of({ refund });
      comp.ngOnInit();

      expect(comp.transactionsCollection).toContain(transaction);
      expect(comp.customersSharedCollection).toContain(customer);
      expect(comp.refund).toEqual(refund);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRefund>>();
      const refund = { id: 123 };
      jest.spyOn(refundFormService, 'getRefund').mockReturnValue(refund);
      jest.spyOn(refundService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ refund });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: refund }));
      saveSubject.complete();

      // THEN
      expect(refundFormService.getRefund).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(refundService.update).toHaveBeenCalledWith(expect.objectContaining(refund));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRefund>>();
      const refund = { id: 123 };
      jest.spyOn(refundFormService, 'getRefund').mockReturnValue({ id: null });
      jest.spyOn(refundService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ refund: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: refund }));
      saveSubject.complete();

      // THEN
      expect(refundFormService.getRefund).toHaveBeenCalled();
      expect(refundService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRefund>>();
      const refund = { id: 123 };
      jest.spyOn(refundService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ refund });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(refundService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTransaction', () => {
      it('Should forward to transactionService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(transactionService, 'compareTransaction');
        comp.compareTransaction(entity, entity2);
        expect(transactionService.compareTransaction).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCustomer', () => {
      it('Should forward to customerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
