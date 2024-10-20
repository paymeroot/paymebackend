import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 6885,
  lastname: 'tant que diminuer dehors',
  firstname: 'certes',
  phone: '0408933599',
  countryCode: 'KG',
};

export const sampleWithPartialData: ICustomer = {
  id: 3951,
  lastname: 'spécialiste même sitôt que',
  firstname: 'touriste',
  phone: '+33 547502557',
  countryCode: 'MY',
  statusKyc: 'PENDING',
};

export const sampleWithFullData: ICustomer = {
  id: 24905,
  lastname: 'même si plier',
  firstname: 'parlementaire patientèle',
  phone: '0499065344',
  countryCode: 'MY',
  statusKyc: 'SUCCESS',
};

export const sampleWithNewData: NewCustomer = {
  lastname: 'accumuler caresser',
  firstname: "incalculable extatique d'abord",
  phone: '+33 747768905',
  countryCode: 'BE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
