export interface IKyc {
  id: number;
  reference?: string | null;
  typePiece?: string | null;
  numberPiece?: string | null;
  photoPieceUrl?: string | null;
  photoSelfieUrl?: string | null;
}

export type NewKyc = Omit<IKyc, 'id'> & { id: null };
