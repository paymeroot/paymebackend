import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IKyc } from '../kyc.model';
import { KycService } from '../service/kyc.service';
import { KycFormService, KycFormGroup } from './kyc-form.service';

@Component({
  standalone: true,
  selector: 'jhi-kyc-update',
  templateUrl: './kyc-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class KycUpdateComponent implements OnInit {
  isSaving = false;
  kyc: IKyc | null = null;

  protected kycService = inject(KycService);
  protected kycFormService = inject(KycFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: KycFormGroup = this.kycFormService.createKycFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ kyc }) => {
      this.kyc = kyc;
      if (kyc) {
        this.updateForm(kyc);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const kyc = this.kycFormService.getKyc(this.editForm);
    if (kyc.id !== null) {
      this.subscribeToSaveResponse(this.kycService.update(kyc));
    } else {
      this.subscribeToSaveResponse(this.kycService.create(kyc));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IKyc>>): void {
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

  protected updateForm(kyc: IKyc): void {
    this.kyc = kyc;
    this.kycFormService.resetForm(this.editForm, kyc);
  }
}
