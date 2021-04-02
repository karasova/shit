import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import MailingTask from './mailing-task';
import MailingTaskDetail from './mailing-task-detail';
import MailingTaskUpdate from './mailing-task-update';
import MailingTaskDeleteDialog from './mailing-task-delete-dialog';

const Routes = ({match}) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={MailingTaskUpdate}/>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={MailingTaskUpdate}/>
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={MailingTaskDetail}/>
      <ErrorBoundaryRoute path={match.url} component={MailingTask}/>
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={MailingTaskDeleteDialog}/>
  </>
);

export default Routes;
