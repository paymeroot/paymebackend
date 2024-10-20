import { IProfile, NewProfile } from './profile.model';

export const sampleWithRequiredData: IProfile = {
  id: 16257,
};

export const sampleWithPartialData: IProfile = {
  id: 18275,
  address: 'passablement jusqu’à ce que',
};

export const sampleWithFullData: IProfile = {
  id: 26756,
  email: 'Remi_Morin@hotmail.fr',
  address: 'entre ha',
  ville: 'photographier',
  countryCode: 'ZW',
  avatarUrl: 'avaler',
  genre: 'FEMALE',
};

export const sampleWithNewData: NewProfile = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
