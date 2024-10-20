import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 'e0bd326f-0d3e-431a-9b3b-306be8656d5d',
  login: 'BgzC',
};

export const sampleWithPartialData: IUser = {
  id: 'bcc7a4b2-b795-45a7-9f5f-1ff827a2f5ee',
  login: 'qugo',
};

export const sampleWithFullData: IUser = {
  id: 'f7de9b6a-4bc8-44cd-960b-9682d5d8caee',
  login: '7',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
