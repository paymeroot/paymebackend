import { IKyc, NewKyc } from './kyc.model';

export const sampleWithRequiredData: IKyc = {
  id: 13658,
  reference: 'si par rapport à',
  typePiece: 'autour de peur que',
  numberPiece: 'aux alentours de oups respecter',
  photoPieceUrl: 'franco',
  photoSelfieUrl: 'bof',
};

export const sampleWithPartialData: IKyc = {
  id: 30240,
  reference: 'aspirer placide',
  typePiece: 'autrefois serviable',
  numberPiece: 'considérable commis miam',
  photoPieceUrl: 'insolite',
  photoSelfieUrl: 'clientèle',
};

export const sampleWithFullData: IKyc = {
  id: 25622,
  reference: 'corps enseignant engendrer désagréable',
  typePiece: 'souple ronron',
  numberPiece: 'pendant que',
  photoPieceUrl: 'veiller',
  photoSelfieUrl: 'chef de cuisine',
};

export const sampleWithNewData: NewKyc = {
  reference: 'tsoin-tsoin',
  typePiece: 'conseil municipal orange ouille',
  numberPiece: 'combler',
  photoPieceUrl: 'de crainte que',
  photoSelfieUrl: 'équipe de recherche altruiste',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
