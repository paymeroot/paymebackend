import { ICountry } from 'app/entities/country/country.model';

export interface IOperator {
  id: number;
  nom?: string | null;
  code?: string | null;
  countryCode?: string | null;
  logoUrl?: string | null;
  taxPayIn?: number | null;
  taxPayOut?: number | null;
  country?: Pick<ICountry, 'id'> | null;
}

export type NewOperator = Omit<IOperator, 'id'> & { id: null };
