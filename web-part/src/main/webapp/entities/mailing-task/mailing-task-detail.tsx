import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Row, Col} from 'reactstrap';
import {Translate, ICrudGetAction, TextFormat} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './mailing-task.reducer';
import {IMailingTask} from 'app/shared/model/mailing-task.model';
import {APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT} from 'app/config/constants';

export interface IMailingTaskDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

export const MailingTaskDetail = (props: IMailingTaskDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const {mailingTaskEntity} = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          <Translate contentKey="botApp.mailingTask.detail.title">MailingTask</Translate> [<b>{mailingTaskEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="plannedTime">
              <Translate contentKey="botApp.mailingTask.plannedTime">Planned Time</Translate>
            </span>
          </dt>
          <dd>
            {mailingTaskEntity.plannedTime ? (
              <TextFormat value={mailingTaskEntity.plannedTime} type="date" format={APP_DATE_FORMAT}/>
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="botApp.mailingTask.status">Status</Translate>
            </span>
          </dt>
          <dd>
            <Translate contentKey={`botApp.MailingStatus.${mailingTaskEntity.status}`}/>
          </dd>
          <dt>
            <span id="type">
              <Translate contentKey="botApp.mailingTask.type">Type</Translate>
            </span>
          </dt>
          <dd>
            <Translate contentKey={`botApp.MailingType.${mailingTaskEntity.type}`}/>
          </dd>
          <dt>
            <span id="filterStatus">
              <Translate contentKey="botApp.mailingTask.filterStatus">Filter Status</Translate>
            </span>
          </dt>
          <dd>{mailingTaskEntity.filterStatus}</dd>
          <dt>
            <span id="message">
              <Translate contentKey="botApp.mailingTask.message">Message</Translate>
            </span>
          </dt>
          <dd>{mailingTaskEntity.message}</dd>
          <dt>
            <Translate contentKey="botApp.mailingTask.filterCase">Filter Case</Translate>
          </dt>
          <dd>{mailingTaskEntity.filterCase ? mailingTaskEntity.filterCase.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/mailing-task" replace color="info">
          <FontAwesomeIcon icon="arrow-left"/>{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/mailing-task/${mailingTaskEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt"/>{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({mailingTask}: IRootState) => ({
  mailingTaskEntity: mailingTask.entity,
});

const mapDispatchToProps = {getEntity};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(MailingTaskDetail);
