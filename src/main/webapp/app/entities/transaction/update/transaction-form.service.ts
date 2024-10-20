import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITransaction, NewTransaction } from '../transaction.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITransaction for edit and NewTransactionFormGroupInput for create.
 */
type TransactionFormGroupInput = ITransaction | PartialWithRequiredKeyOf<NewTransaction>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITransaction | NewTransaction> = Omit<T, 'transactionDate'> & {
  transactionDate?: string | null;
};

type TransactionFormRawValue = FormValueOf<ITransaction>;

type NewTransactionFormRawValue = FormValueOf<NewTransaction>;

type TransactionFormDefaults = Pick<NewTransaction, 'id' | 'transactionDate'>;

type TransactionFormGroupContent = {
  id: FormControl<TransactionFormRawValue['id'] | NewTransaction['id']>;
  reference: FormControl<TransactionFormRawValue['reference']>;
  transactionDate: FormControl<TransactionFormRawValue['transactionDate']>;
  senderNumber: FormControl<TransactionFormRawValue['senderNumber']>;
  senderWallet: FormControl<TransactionFormRawValue['senderWallet']>;
  receiverNumber: FormControl<TransactionFormRawValue['receiverNumber']>;
  receiverWallet: FormControl<TransactionFormRawValue['receiverWallet']>;
  transactionStatus: FormControl<TransactionFormRawValue['transactionStatus']>;
  payInStatus: FormControl<TransactionFormRawValue['payInStatus']>;
  payOutStatus: FormControl<TransactionFormRawValue['payOutStatus']>;
  amount: FormControl<TransactionFormRawValue['amount']>;
  object: FormControl<TransactionFormRawValue['object']>;
  payInFailureReason: FormControl<TransactionFormRawValue['payInFailureReason']>;
  payOutFailureReason: FormControl<TransactionFormRawValue['payOutFailureReason']>;
  senderCountryName: FormControl<TransactionFormRawValue['senderCountryName']>;
  receiverCountryName: FormControl<TransactionFormRawValue['receiverCountryName']>;
  customer: FormControl<TransactionFormRawValue['customer']>;
};

export type TransactionFormGroup = FormGroup<TransactionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TransactionFormService {
  createTransactionFormGroup(transaction: TransactionFormGroupInput = { id: null }): TransactionFormGroup {
    const transactionRawValue = this.convertTransactionToTransactionRawValue({
      ...this.getFormDefaults(),
      ...transaction,
    });
    return new FormGroup<TransactionFormGroupContent>({
      id: new FormControl(
        { value: transactionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(transactionRawValue.reference, {
        validators: [Validators.required],
      }),
      transactionDate: new FormControl(transactionRawValue.transactionDate, {
        validators: [Validators.required],
      }),
      senderNumber: new FormControl(transactionRawValue.senderNumber, {
        validators: [Validators.required],
      }),
      senderWallet: new FormControl(transactionRawValue.senderWallet, {
        validators: [Validators.required],
      }),
      receiverNumber: new FormControl(transactionRawValue.receiverNumber, {
        validators: [Validators.required],
      }),
      receiverWallet: new FormControl(transactionRawValue.receiverWallet, {
        validators: [Validators.required],
      }),
      transactionStatus: new FormControl(transactionRawValue.transactionStatus, {
        validators: [Validators.required],
      }),
      payInStatus: new FormControl(transactionRawValue.payInStatus, {
        validators: [Validators.required],
      }),
      payOutStatus: new FormControl(transactionRawValue.payOutStatus, {
        validators: [Validators.required],
      }),
      amount: new FormControl(transactionRawValue.amount, {
        validators: [Validators.required],
      }),
      object: new FormControl(transactionRawValue.object),
      payInFailureReason: new FormControl(transactionRawValue.payInFailureReason),
      payOutFailureReason: new FormControl(transactionRawValue.payOutFailureReason),
      senderCountryName: new FormControl(transactionRawValue.senderCountryName),
      receiverCountryName: new FormControl(transactionRawValue.receiverCountryName),
      customer: new FormControl(transactionRawValue.customer),
    });
  }

  getTransaction(form: TransactionFormGroup): ITransaction | NewTransaction {
    return this.convertTransactionRawValueToTransaction(form.getRawValue() as TransactionFormRawValue | NewTransactionFormRawValue);
  }

  resetForm(form: TransactionFormGroup, transaction: TransactionFormGroupInput): void {
    const transactionRawValue = this.convertTransactionToTransactionRawValue({ ...this.getFormDefaults(), ...transaction });
    form.reset(
      {
        ...transactionRawValue,
        id: { value: transactionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TransactionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      transactionDate: currentTime,
    };
  }

  private convertTransactionRawValueToTransaction(
    rawTransaction: TransactionFormRawValue | NewTransactionFormRawValue,
  ): ITransaction | NewTransaction {
    return {
      ...rawTransaction,
      transactionDate: dayjs(rawTransaction.transactionDate, DATE_TIME_FORMAT),
    };
  }

  private convertTransactionToTransactionRawValue(
    transaction: ITransaction | (Partial<NewTransaction> & TransactionFormDefaults),
  ): TransactionFormRawValue | PartialWithRequiredKeyOf<NewTransactionFormRawValue> {
    return {
      ...transaction,
      transactionDate: transaction.transactionDate ? transaction.transactionDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
