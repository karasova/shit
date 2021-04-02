import { IParticipant } from 'app/shared/model/participant.model';
import { ITrack } from 'app/shared/model/track.model';
import { TeamStatus } from 'app/shared/model/enumerations/team-status.model';

export interface ITeam {
  id?: number;
  title?: string;
  status?: TeamStatus;
  comment?: string;
  registrator?: IParticipant;
  participants?: IParticipant[];
  case?: ITrack;
}

export const defaultValue: Readonly<ITeam> = {};
