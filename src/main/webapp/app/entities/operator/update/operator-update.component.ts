import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICountry } from 'app/entities/country/country.model';
import { CountryService } from 'app/entities/country/service/country.service';
import { IOperator } from '../operator.model';
import { OperatorService } from '../service/operator.service';
import { OperatorFormService, OperatorFormGroup } from './operator-form.service';

@Component({
  standalone: true,
  selector: 'jhi-operator-update',
  templateUrl: './operator-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class OperatorUpdateComponent implements OnInit {
  isSaving = false;
  operator: IOperator | null = null;

  countriesCollection: ICountry[] = [];

  protected operatorService = inject(OperatorService);
  protected operatorFormService = inject(OperatorFormService);
  protected countryService = inject(CountryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: OperatorFormGroup = this.operatorFormService.createOperatorFormGroup();

  compareCountry = (o1: ICountry | null, o2: ICountry | null): boolean => this.countryService.compareCountry(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ operator }) => {
      this.operator = operator;
      if (operator) {
        this.updateForm(operator);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const operator = this.operatorFormService.getOperator(this.editForm);
    if (operator.id !== null) {
      this.subscribeToSaveResponse(this.operatorService.update(operator));
    } else {
      this.subscribeToSaveResponse(this.operatorService.create(operator));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOperator>>): void {
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

  protected updateForm(operator: IOperator): void {
    this.operator = operator;
    this.operatorFormService.resetForm(this.editForm, operator);

    this.countriesCollection = this.countryService.addCountryToCollectionIfMissing<ICountry>(this.countriesCollection, operator.country);
  }

  protected loadRelationshipsOptions(): void {
    this.countryService
      .query({ filter: 'operator-is-null' })
      .pipe(map((res: HttpResponse<ICountry[]>) => res.body ?? []))
      .pipe(
        map((countries: ICountry[]) => this.countryService.addCountryToCollectionIfMissing<ICountry>(countries, this.operator?.country)),
      )
      .subscribe((countries: ICountry[]) => (this.countriesCollection = countries));
  }
}
