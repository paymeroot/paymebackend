import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { IKyc } from 'app/entities/kyc/kyc.model';
import { KycService } from 'app/entities/kyc/service/kyc.service';
import { ICountry } from 'app/entities/country/country.model';
import { CountryService } from 'app/entities/country/service/country.service';
import { Status } from 'app/entities/enumerations/status.model';
import { CustomerService } from '../service/customer.service';
import { ICustomer } from '../customer.model';
import { CustomerFormService, CustomerFormGroup } from './customer-form.service';

@Component({
  standalone: true,
  selector: 'jhi-customer-update',
  templateUrl: './customer-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CustomerUpdateComponent implements OnInit {
  isSaving = false;
  customer: ICustomer | null = null;
  statusValues = Object.keys(Status);

  profilesCollection: IProfile[] = [];
  kycsCollection: IKyc[] = [];
  countriesCollection: ICountry[] = [];

  protected customerService = inject(CustomerService);
  protected customerFormService = inject(CustomerFormService);
  protected profileService = inject(ProfileService);
  protected kycService = inject(KycService);
  protected countryService = inject(CountryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CustomerFormGroup = this.customerFormService.createCustomerFormGroup();

  compareProfile = (o1: IProfile | null, o2: IProfile | null): boolean => this.profileService.compareProfile(o1, o2);

  compareKyc = (o1: IKyc | null, o2: IKyc | null): boolean => this.kycService.compareKyc(o1, o2);

  compareCountry = (o1: ICountry | null, o2: ICountry | null): boolean => this.countryService.compareCountry(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customer }) => {
      this.customer = customer;
      if (customer) {
        this.updateForm(customer);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const customer = this.customerFormService.getCustomer(this.editForm);
    if (customer.id !== null) {
      this.subscribeToSaveResponse(this.customerService.update(customer));
    } else {
      this.subscribeToSaveResponse(this.customerService.create(customer));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICustomer>>): void {
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

  protected updateForm(customer: ICustomer): void {
    this.customer = customer;
    this.customerFormService.resetForm(this.editForm, customer);

    this.profilesCollection = this.profileService.addProfileToCollectionIfMissing<IProfile>(this.profilesCollection, customer.profile);
    this.kycsCollection = this.kycService.addKycToCollectionIfMissing<IKyc>(this.kycsCollection, customer.kyc);
    this.countriesCollection = this.countryService.addCountryToCollectionIfMissing<ICountry>(this.countriesCollection, customer.country);
  }

  protected loadRelationshipsOptions(): void {
    this.profileService
      .query({ 'customerId.specified': 'false' })
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(map((profiles: IProfile[]) => this.profileService.addProfileToCollectionIfMissing<IProfile>(profiles, this.customer?.profile)))
      .subscribe((profiles: IProfile[]) => (this.profilesCollection = profiles));

    this.kycService
      .query({ 'customerId.specified': 'false' })
      .pipe(map((res: HttpResponse<IKyc[]>) => res.body ?? []))
      .pipe(map((kycs: IKyc[]) => this.kycService.addKycToCollectionIfMissing<IKyc>(kycs, this.customer?.kyc)))
      .subscribe((kycs: IKyc[]) => (this.kycsCollection = kycs));

    this.countryService
      .query({ filter: 'customer-is-null' })
      .pipe(map((res: HttpResponse<ICountry[]>) => res.body ?? []))
      .pipe(
        map((countries: ICountry[]) => this.countryService.addCountryToCollectionIfMissing<ICountry>(countries, this.customer?.country)),
      )
      .subscribe((countries: ICountry[]) => (this.countriesCollection = countries));
  }
}
