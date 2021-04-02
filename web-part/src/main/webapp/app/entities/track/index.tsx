import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Track from './track';
import TrackDetail from './track-detail';
import TrackUpdate from './track-update';
import TrackDeleteDialog from './track-delete-dialog';

const Routes = ({match}) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TrackUpdate}/>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TrackUpdate}/>
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TrackDetail}/>
      <ErrorBoundaryRoute path={match.url} component={Track}/>
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={TrackDeleteDialog}/>
  </>
);

export default Routes;
