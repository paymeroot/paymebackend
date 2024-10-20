import { IOperator, NewOperator } from './operator.model';

export const sampleWithRequiredData: IOperator = {
  id: 32287,
  nom: 'glouglou plaindre',
  code: 'adorable longtemps dans la mesure où',
  countryCode: 'TM',
};

export const sampleWithPartialData: IOperator = {
  id: 10006,
  nom: 'tandis que électorat pin-pon',
  code: 'gestionnaire que atchoum',
  countryCode: 'AD',
  taxPayIn: 17061.9,
};

export const sampleWithFullData: IOperator = {
  id: 24518,
  nom: 'mince vouh mieux',
  code: 'au point que aider puis',
  countryCode: 'CG',
  logoUrl: 'plic assurément',
  taxPayIn: 20910.13,
  taxPayOut: 29236.3,
};

export const sampleWithNewData: NewOperator = {
  nom: 'ensuite souple partenaire',
  code: 'diététiste',
  countryCode: 'BD',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
