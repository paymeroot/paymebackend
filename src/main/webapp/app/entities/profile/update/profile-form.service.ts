import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProfile, NewProfile } from '../profile.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfile for edit and NewProfileFormGroupInput for create.
 */
type ProfileFormGroupInput = IProfile | PartialWithRequiredKeyOf<NewProfile>;

type ProfileFormDefaults = Pick<NewProfile, 'id'>;

type ProfileFormGroupContent = {
  id: FormControl<IProfile['id'] | NewProfile['id']>;
  email: FormControl<IProfile['email']>;
  address: FormControl<IProfile['address']>;
  ville: FormControl<IProfile['ville']>;
  countryCode: FormControl<IProfile['countryCode']>;
  avatarUrl: FormControl<IProfile['avatarUrl']>;
  genre: FormControl<IProfile['genre']>;
};

export type ProfileFormGroup = FormGroup<ProfileFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProfileFormService {
  createProfileFormGroup(profile: ProfileFormGroupInput = { id: null }): ProfileFormGroup {
    const profileRawValue = {
      ...this.getFormDefaults(),
      ...profile,
    };
    return new FormGroup<ProfileFormGroupContent>({
      id: new FormControl(
        { value: profileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      email: new FormControl(profileRawValue.email),
      address: new FormControl(profileRawValue.address),
      ville: new FormControl(profileRawValue.ville),
      countryCode: new FormControl(profileRawValue.countryCode),
      avatarUrl: new FormControl(profileRawValue.avatarUrl),
      genre: new FormControl(profileRawValue.genre),
    });
  }

  getProfile(form: ProfileFormGroup): IProfile | NewProfile {
    return form.getRawValue() as IProfile | NewProfile;
  }

  resetForm(form: ProfileFormGroup, profile: ProfileFormGroupInput): void {
    const profileRawValue = { ...this.getFormDefaults(), ...profile };
    form.reset(
      {
        ...profileRawValue,
        id: { value: profileRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProfileFormDefaults {
    return {
      id: null,
    };
  }
}
