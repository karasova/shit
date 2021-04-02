import React from 'react';
import {Switch} from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Team from './team';
import Participant from './participant';
import Track from './track';
import MailingTask from './mailing-task';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({match}) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}team`} component={Team}/>
      <ErrorBoundaryRoute path={`${match.url}participant`} component={Participant}/>
      <ErrorBoundaryRoute path={`${match.url}track`} component={Track}/>
      <ErrorBoundaryRoute path={`${match.url}mailing-task`} component={MailingTask}/>
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
