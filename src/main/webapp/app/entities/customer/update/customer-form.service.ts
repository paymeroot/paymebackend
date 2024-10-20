import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICustomer, NewCustomer } from '../customer.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICustomer for edit and NewCustomerFormGroupInput for create.
 */
type CustomerFormGroupInput = ICustomer | PartialWithRequiredKeyOf<NewCustomer>;

type CustomerFormDefaults = Pick<NewCustomer, 'id'>;

type CustomerFormGroupContent = {
  id: FormControl<ICustomer['id'] | NewCustomer['id']>;
  lastname: FormControl<ICustomer['lastname']>;
  firstname: FormControl<ICustomer['firstname']>;
  phone: FormControl<ICustomer['phone']>;
  countryCode: FormControl<ICustomer['countryCode']>;
  statusKyc: FormControl<ICustomer['statusKyc']>;
  profile: FormControl<ICustomer['profile']>;
  kyc: FormControl<ICustomer['kyc']>;
  country: FormControl<ICustomer['country']>;
};

export type CustomerFormGroup = FormGroup<CustomerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CustomerFormService {
  createCustomerFormGroup(customer: CustomerFormGroupInput = { id: null }): CustomerFormGroup {
    const customerRawValue = {
      ...this.getFormDefaults(),
      ...customer,
    };
    return new FormGroup<CustomerFormGroupContent>({
      id: new FormControl(
        { value: customerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      lastname: new FormControl(customerRawValue.lastname, {
        validators: [Validators.required],
      }),
      firstname: new FormControl(customerRawValue.firstname, {
        validators: [Validators.required],
      }),
      phone: new FormControl(customerRawValue.phone, {
        validators: [Validators.required],
      }),
      countryCode: new FormControl(customerRawValue.countryCode, {
        validators: [Validators.required],
      }),
      statusKyc: new FormControl(customerRawValue.statusKyc),
      profile: new FormControl(customerRawValue.profile),
      kyc: new FormControl(customerRawValue.kyc),
      country: new FormControl(customerRawValue.country),
    });
  }

  getCustomer(form: CustomerFormGroup): ICustomer | NewCustomer {
    return form.getRawValue() as ICustomer | NewCustomer;
  }

  resetForm(form: CustomerFormGroup, customer: CustomerFormGroupInput): void {
    const customerRawValue = { ...this.getFormDefaults(), ...customer };
    form.reset(
      {
        ...customerRawValue,
        id: { value: customerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CustomerFormDefaults {
    return {
      id: null,
    };
  }
}
