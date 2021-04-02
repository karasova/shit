import { Moment } from 'moment';
import { ITrack } from 'app/shared/model/track.model';
import { TeamStatus } from 'app/shared/model/enumerations/team-status.model';
import { MailingStatus } from 'app/shared/model/enumerations/mailing-status.model';
import { MailingType } from 'app/shared/model/enumerations/mailing-type.model';

export interface IMailingTask {
  id?: number;
  status?: MailingStatus;
  type?: MailingType;
  plannedTime?: string;
  filterStatus?: TeamStatus;
  message?: string;
  filterCase?: ITrack;
}

export const defaultValue: Readonly<IMailingTask> = {};
