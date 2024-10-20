import dayjs from 'dayjs/esm';

import { IRefund, NewRefund } from './refund.model';

export const sampleWithRequiredData: IRefund = {
  id: 29416,
  reference: 'snif a plus',
  transactionRef: 'couper de peur que',
  refundDate: dayjs('2024-10-19T21:42'),
  amount: 24662.04,
};

export const sampleWithPartialData: IRefund = {
  id: 29770,
  reference: 'avex voir miam',
  transactionRef: 'personnel professionnel pour que ouah',
  refundDate: dayjs('2024-10-19T14:02'),
  refundStatus: 'SUCCESS',
  amount: 5395.19,
};

export const sampleWithFullData: IRefund = {
  id: 31254,
  reference: 'coin-coin',
  transactionRef: 'gigantesque badaboum',
  refundDate: dayjs('2024-10-19T04:05'),
  refundStatus: 'SUCCESS',
  amount: 18358.28,
};

export const sampleWithNewData: NewRefund = {
  reference: 'membre titulaire concernant à raison de',
  transactionRef: 'tellement membre titulaire',
  refundDate: dayjs('2024-10-19T20:57'),
  amount: 5424.12,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
