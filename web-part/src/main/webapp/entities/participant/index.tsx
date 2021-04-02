import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Participant from './participant';
import ParticipantDetail from './participant-detail';
import ParticipantUpdate from './participant-update';
import ParticipantDeleteDialog from './participant-delete-dialog';

const Routes = ({match}) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ParticipantUpdate}/>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ParticipantUpdate}/>
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ParticipantDetail}/>
      <ErrorBoundaryRoute path={match.url} component={Participant}/>
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ParticipantDeleteDialog}/>
  </>
);

export default Routes;
