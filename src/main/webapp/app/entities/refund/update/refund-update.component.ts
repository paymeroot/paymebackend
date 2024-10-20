import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITransaction } from 'app/entities/transaction/transaction.model';
import { TransactionService } from 'app/entities/transaction/service/transaction.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { Status } from 'app/entities/enumerations/status.model';
import { RefundService } from '../service/refund.service';
import { IRefund } from '../refund.model';
import { RefundFormService, RefundFormGroup } from './refund-form.service';

@Component({
  standalone: true,
  selector: 'jhi-refund-update',
  templateUrl: './refund-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class RefundUpdateComponent implements OnInit {
  isSaving = false;
  refund: IRefund | null = null;
  statusValues = Object.keys(Status);

  transactionsCollection: ITransaction[] = [];
  customersSharedCollection: ICustomer[] = [];

  protected refundService = inject(RefundService);
  protected refundFormService = inject(RefundFormService);
  protected transactionService = inject(TransactionService);
  protected customerService = inject(CustomerService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RefundFormGroup = this.refundFormService.createRefundFormGroup();

  compareTransaction = (o1: ITransaction | null, o2: ITransaction | null): boolean => this.transactionService.compareTransaction(o1, o2);

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ refund }) => {
      this.refund = refund;
      if (refund) {
        this.updateForm(refund);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const refund = this.refundFormService.getRefund(this.editForm);
    if (refund.id !== null) {
      this.subscribeToSaveResponse(this.refundService.update(refund));
    } else {
      this.subscribeToSaveResponse(this.refundService.create(refund));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRefund>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(refund: IRefund): void {
    this.refund = refund;
    this.refundFormService.resetForm(this.editForm, refund);

    this.transactionsCollection = this.transactionService.addTransactionToCollectionIfMissing<ITransaction>(
      this.transactionsCollection,
      refund.transaction,
    );
    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing<ICustomer>(
      this.customersSharedCollection,
      refund.customer,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.transactionService
      .query({ 'refundId.specified': 'false' })
      .pipe(map((res: HttpResponse<ITransaction[]>) => res.body ?? []))
      .pipe(
        map((transactions: ITransaction[]) =>
          this.transactionService.addTransactionToCollectionIfMissing<ITransaction>(transactions, this.refund?.transaction),
        ),
      )
      .subscribe((transactions: ITransaction[]) => (this.transactionsCollection = transactions));

    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) => this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.refund?.customer)),
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));
  }
}
