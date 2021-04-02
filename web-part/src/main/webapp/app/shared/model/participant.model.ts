import { ITeam } from 'app/shared/model/team.model';

export interface IParticipant {
  id?: number;
  vkId?: number;
  fullName?: string;
  age?: number;
  employer?: string;
  phoneNumber?: string;
  team?: ITeam;
}

export const defaultValue: Readonly<IParticipant> = {};
