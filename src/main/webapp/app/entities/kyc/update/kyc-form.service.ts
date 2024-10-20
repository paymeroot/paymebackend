import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IKyc, NewKyc } from '../kyc.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IKyc for edit and NewKycFormGroupInput for create.
 */
type KycFormGroupInput = IKyc | PartialWithRequiredKeyOf<NewKyc>;

type KycFormDefaults = Pick<NewKyc, 'id'>;

type KycFormGroupContent = {
  id: FormControl<IKyc['id'] | NewKyc['id']>;
  reference: FormControl<IKyc['reference']>;
  typePiece: FormControl<IKyc['typePiece']>;
  numberPiece: FormControl<IKyc['numberPiece']>;
  photoPieceUrl: FormControl<IKyc['photoPieceUrl']>;
  photoSelfieUrl: FormControl<IKyc['photoSelfieUrl']>;
};

export type KycFormGroup = FormGroup<KycFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class KycFormService {
  createKycFormGroup(kyc: KycFormGroupInput = { id: null }): KycFormGroup {
    const kycRawValue = {
      ...this.getFormDefaults(),
      ...kyc,
    };
    return new FormGroup<KycFormGroupContent>({
      id: new FormControl(
        { value: kycRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(kycRawValue.reference, {
        validators: [Validators.required],
      }),
      typePiece: new FormControl(kycRawValue.typePiece, {
        validators: [Validators.required],
      }),
      numberPiece: new FormControl(kycRawValue.numberPiece, {
        validators: [Validators.required],
      }),
      photoPieceUrl: new FormControl(kycRawValue.photoPieceUrl, {
        validators: [Validators.required],
      }),
      photoSelfieUrl: new FormControl(kycRawValue.photoSelfieUrl, {
        validators: [Validators.required],
      }),
    });
  }

  getKyc(form: KycFormGroup): IKyc | NewKyc {
    return form.getRawValue() as IKyc | NewKyc;
  }

  resetForm(form: KycFormGroup, kyc: KycFormGroupInput): void {
    const kycRawValue = { ...this.getFormDefaults(), ...kyc };
    form.reset(
      {
        ...kycRawValue,
        id: { value: kycRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): KycFormDefaults {
    return {
      id: null,
    };
  }
}
