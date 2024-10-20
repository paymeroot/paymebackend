import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRefund, NewRefund } from '../refund.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRefund for edit and NewRefundFormGroupInput for create.
 */
type RefundFormGroupInput = IRefund | PartialWithRequiredKeyOf<NewRefund>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRefund | NewRefund> = Omit<T, 'refundDate'> & {
  refundDate?: string | null;
};

type RefundFormRawValue = FormValueOf<IRefund>;

type NewRefundFormRawValue = FormValueOf<NewRefund>;

type RefundFormDefaults = Pick<NewRefund, 'id' | 'refundDate'>;

type RefundFormGroupContent = {
  id: FormControl<RefundFormRawValue['id'] | NewRefund['id']>;
  reference: FormControl<RefundFormRawValue['reference']>;
  transactionRef: FormControl<RefundFormRawValue['transactionRef']>;
  refundDate: FormControl<RefundFormRawValue['refundDate']>;
  refundStatus: FormControl<RefundFormRawValue['refundStatus']>;
  amount: FormControl<RefundFormRawValue['amount']>;
  transaction: FormControl<RefundFormRawValue['transaction']>;
  customer: FormControl<RefundFormRawValue['customer']>;
};

export type RefundFormGroup = FormGroup<RefundFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RefundFormService {
  createRefundFormGroup(refund: RefundFormGroupInput = { id: null }): RefundFormGroup {
    const refundRawValue = this.convertRefundToRefundRawValue({
      ...this.getFormDefaults(),
      ...refund,
    });
    return new FormGroup<RefundFormGroupContent>({
      id: new FormControl(
        { value: refundRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(refundRawValue.reference, {
        validators: [Validators.required],
      }),
      transactionRef: new FormControl(refundRawValue.transactionRef, {
        validators: [Validators.required],
      }),
      refundDate: new FormControl(refundRawValue.refundDate, {
        validators: [Validators.required],
      }),
      refundStatus: new FormControl(refundRawValue.refundStatus),
      amount: new FormControl(refundRawValue.amount, {
        validators: [Validators.required],
      }),
      transaction: new FormControl(refundRawValue.transaction),
      customer: new FormControl(refundRawValue.customer),
    });
  }

  getRefund(form: RefundFormGroup): IRefund | NewRefund {
    return this.convertRefundRawValueToRefund(form.getRawValue() as RefundFormRawValue | NewRefundFormRawValue);
  }

  resetForm(form: RefundFormGroup, refund: RefundFormGroupInput): void {
    const refundRawValue = this.convertRefundToRefundRawValue({ ...this.getFormDefaults(), ...refund });
    form.reset(
      {
        ...refundRawValue,
        id: { value: refundRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): RefundFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      refundDate: currentTime,
    };
  }

  private convertRefundRawValueToRefund(rawRefund: RefundFormRawValue | NewRefundFormRawValue): IRefund | NewRefund {
    return {
      ...rawRefund,
      refundDate: dayjs(rawRefund.refundDate, DATE_TIME_FORMAT),
    };
  }

  private convertRefundToRefundRawValue(
    refund: IRefund | (Partial<NewRefund> & RefundFormDefaults),
  ): RefundFormRawValue | PartialWithRequiredKeyOf<NewRefundFormRawValue> {
    return {
      ...refund,
      refundDate: refund.refundDate ? refund.refundDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
