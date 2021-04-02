import { ITeam } from 'app/shared/model/team.model';
import { IMailingTask } from 'app/shared/model/mailing-task.model';

export interface ITrack {
  id?: number;
  title?: string;
  teams?: ITeam[];
  derived?: ITrackDerived;
  mailingTasks?: IMailingTask[];
}

export interface ITrackDerived {
  count?: number;
  remaining?: number;
  max?: number;
}

export const defaultValue: Readonly<ITrack> = {};
