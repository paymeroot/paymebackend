import { ICountry, NewCountry } from './country.model';

export const sampleWithRequiredData: ICountry = {
  id: 18942,
  code: 'rédaction spécialiste',
  name: 'trop',
};

export const sampleWithPartialData: ICountry = {
  id: 31102,
  code: 'comme sauvage',
  name: 'déplacer',
  logoUrl: 'équipe biathlète',
};

export const sampleWithFullData: ICountry = {
  id: 25777,
  code: 'ressembler',
  name: 'tellement',
  logoUrl: 'à cause de de sorte que',
};

export const sampleWithNewData: NewCountry = {
  code: 'vétuste',
  name: 'au-delà membre du personnel pendant que',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
