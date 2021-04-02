import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Team from './team';
import TeamDetail from './team-detail';
import TeamUpdate from './team-update';
import TeamDeleteDialog from './team-delete-dialog';
import TeamImport from "app/entities/team/team-import";

const Routes = ({match}) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TeamUpdate}/>
      <ErrorBoundaryRoute exact path={`${match.url}/import`} component={TeamImport}/>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TeamUpdate}/>
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TeamDetail}/>
      <ErrorBoundaryRoute path={match.url} component={Team}/>
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={TeamDeleteDialog}/>
  </>
);

export default Routes;
