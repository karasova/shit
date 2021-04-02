import React, {useState, useEffect} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Row, Col, Label} from 'reactstrap';
import {AvFeedback, AvForm, AvGroup, AvInput, AvField} from 'availity-reactstrap-validation';
import {Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';

import {ITrack} from 'app/shared/model/track.model';
import {getEntities as getTracks} from 'app/entities/track/track.reducer';
import {getEntity, updateEntity, createEntity, reset} from './mailing-task.reducer';
import {IMailingTask} from 'app/shared/model/mailing-task.model';
import {convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime} from 'app/shared/util/date-utils';
import {mapIdList} from 'app/shared/util/entity-utils';

export interface IMailingTaskUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

export const MailingTaskUpdate = (props: IMailingTaskUpdateProps) => {
  const [filterCaseId, setFilterCaseId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const {mailingTaskEntity, tracks, loading, updating} = props;

  const handleClose = () => {
    props.history.push('/mailing-task');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getTracks();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    values.plannedTime = convertDateTimeToServer(values.plannedTime);

    if (errors.length === 0) {
      const entity = {
        ...mailingTaskEntity,
        ...values,
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="botApp.mailingTask.home.createOrEditLabel">
            <Translate contentKey="botApp.mailingTask.home.createOrEditLabel">Create or edit a MailingTask</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : mailingTaskEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="mailing-task-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="mailing-task-id" type="text" className="form-control" name="id" required readOnly/>
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="plannedTimeLabel" for="mailing-task-plannedTime">
                  <Translate contentKey="botApp.mailingTask.plannedTime">Planned Time</Translate>
                </Label>
                <AvInput
                  id="mailing-task-plannedTime"
                  type="datetime-local"
                  className="form-control"
                  name="plannedTime"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.mailingTaskEntity.plannedTime)}
                  validate={{
                    required: {value: true, errorMessage: translate('entity.validation.required')},
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="filterStatusLabel" for="mailing-task-filterStatus">
                  <Translate contentKey="botApp.mailingTask.filterStatus">Filter Status</Translate>
                </Label>
                <AvInput
                  id="mailing-task-filterStatus"
                  type="select"
                  className="form-control"
                  name="filterStatus"
                  value={(!isNew && mailingTaskEntity.filterStatus) || 'ADDED'}
                >
                  <option value="ADDED">{translate('botApp.TeamStatus.ADDED')}</option>
                  <option value="CASE_SELECTION">{translate('botApp.TeamStatus.CASE_SELECTION')}</option>
                  <option value="CASE_SELECTED">{translate('botApp.TeamStatus.CASE_SELECTED')}</option>
                  <option value="REGISTRATION">{translate('botApp.TeamStatus.REGISTRATION')}</option>
                  <option value="REGISTERED">{translate('botApp.TeamStatus.REGISTERED')}</option>
                  <option value="PARTICIPANTS_NEEDED">{translate('botApp.TeamStatus.PARTICIPANTS_NEEDED')}</option>
                  <option value="CANCELED">{translate('botApp.TeamStatus.CANCELED')}</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="typeLabel" for="mailing-task-type">
                  <Translate contentKey="botApp.mailingTask.type">Type</Translate>
                </Label>
                <AvInput
                  id="mailing-task-type"
                  type="select"
                  className="form-control"
                  name="type"
                  value={(!isNew && mailingTaskEntity.type) || 'STANDARD'}
                >
                  <option value="STANDARD">{translate('botApp.MailingType.STANDARD')}</option>
                  <option value="SELECT_CASE">{translate('botApp.MailingType.SELECT_CASE')}</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="messageLabel" for="mailing-task-message">
                  <Translate contentKey="botApp.mailingTask.message">Message</Translate>
                </Label>
                <AvField
                  id="mailing-task-message"
                  type="textarea"
                  name="message"
                  validate={{
                    required: {value: true, errorMessage: translate('entity.validation.required')},
                    minLength: {value: 1, errorMessage: translate('entity.validation.minlength', {min: 1})},
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label for="mailing-task-filterCase">
                  <Translate contentKey="botApp.mailingTask.filterCase">Filter Case</Translate>
                </Label>
                <AvInput id="mailing-task-filterCase" type="select" className="form-control" name="filterCase.id">
                  <option value="" key="0"/>
                  {tracks
                    ? tracks.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/mailing-task" replace color="info">
                <FontAwesomeIcon icon="arrow-left"/>
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save"/>
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  tracks: storeState.track.entities,
  mailingTaskEntity: storeState.mailingTask.entity,
  loading: storeState.mailingTask.loading,
  updating: storeState.mailingTask.updating,
  updateSuccess: storeState.mailingTask.updateSuccess,
});

const mapDispatchToProps = {
  getTracks,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(MailingTaskUpdate);
