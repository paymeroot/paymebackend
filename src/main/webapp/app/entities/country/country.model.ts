export interface ICountry {
  id: number;
  code?: string | null;
  name?: string | null;
  logoUrl?: string | null;
}

export type NewCountry = Omit<ICountry, 'id'> & { id: null };
