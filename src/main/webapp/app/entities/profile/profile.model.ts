import { Gender } from 'app/entities/enumerations/gender.model';

export interface IProfile {
  id: number;
  email?: string | null;
  address?: string | null;
  ville?: string | null;
  countryCode?: string | null;
  avatarUrl?: string | null;
  genre?: keyof typeof Gender | null;
}

export type NewProfile = Omit<IProfile, 'id'> & { id: null };
