import dayjs from 'dayjs/esm';

import { ITransaction, NewTransaction } from './transaction.model';

export const sampleWithRequiredData: ITransaction = {
  id: 31868,
  reference: 'remplacer infiniment',
  transactionDate: dayjs('2024-10-19T04:20'),
  senderNumber: 'puisque pis puis',
  senderWallet: 'de façon à',
  receiverNumber: 'jamais dynamique',
  receiverWallet: "esquisser désagréable d'abord",
  transactionStatus: 'FAILURE',
  payInStatus: 'PENDING',
  payOutStatus: 'PENDING',
  amount: 31024.54,
};

export const sampleWithPartialData: ITransaction = {
  id: 23698,
  reference: 'terriblement au cas où',
  transactionDate: dayjs('2024-10-19T14:31'),
  senderNumber: 'entourer',
  senderWallet: 'soutenir pour toutefois',
  receiverNumber: 'exprès en faveur de si bien que',
  receiverWallet: 'snob',
  transactionStatus: 'PENDING',
  payInStatus: 'PENDING',
  payOutStatus: 'PENDING',
  amount: 29397.03,
  object: 'enfin',
  payInFailureReason: 'triathlète passablement étonner',
  payOutFailureReason: 'aux alentours de contraindre infiniment',
  senderCountryName: 'adversaire approximativement au prix de',
  receiverCountryName: 'puisque',
};

export const sampleWithFullData: ITransaction = {
  id: 24923,
  reference: 'avare',
  transactionDate: dayjs('2024-10-19T20:25'),
  senderNumber: 'après que souple',
  senderWallet: 'cesser commis de cuisine innombrable',
  receiverNumber: 'tremper',
  receiverWallet: 'manier',
  transactionStatus: 'PENDING',
  payInStatus: 'SUCCESS',
  payOutStatus: 'PENDING',
  amount: 1906.19,
  object: 'élever céans communauté étudiante',
  payInFailureReason: 'souvent sans que',
  payOutFailureReason: "à l'entour de chef certes",
  senderCountryName: 'à condition que franchir',
  receiverCountryName: 'antagoniste ha ha sage',
};

export const sampleWithNewData: NewTransaction = {
  reference: 'snob géométrique',
  transactionDate: dayjs('2024-10-19T08:42'),
  senderNumber: 'tic-tac hormis',
  senderWallet: 'drelin à bas de',
  receiverNumber: 'juriste',
  receiverWallet: 'éclairer derrière si',
  transactionStatus: 'SUCCESS',
  payInStatus: 'FAILURE',
  payOutStatus: 'FAILURE',
  amount: 23354.14,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
