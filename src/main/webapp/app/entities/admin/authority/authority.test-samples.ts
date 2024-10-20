import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '97fdcdf0-bf07-4616-aaa1-86e95385b673',
};

export const sampleWithPartialData: IAuthority = {
  name: '8e858b37-0c88-4660-bf9e-08e0f6333a1f',
};

export const sampleWithFullData: IAuthority = {
  name: '3cf43417-84ae-4005-9873-03242f9bcc60',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
