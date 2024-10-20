import { IProfile } from 'app/entities/profile/profile.model';
import { IKyc } from 'app/entities/kyc/kyc.model';
import { ICountry } from 'app/entities/country/country.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface ICustomer {
  id: number;
  lastname?: string | null;
  firstname?: string | null;
  phone?: string | null;
  countryCode?: string | null;
  statusKyc?: keyof typeof Status | null;
  profile?: Pick<IProfile, 'id'> | null;
  kyc?: Pick<IKyc, 'id'> | null;
  country?: Pick<ICountry, 'id'> | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
