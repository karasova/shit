import React, {useState, useEffect} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Row, Col, Label} from 'reactstrap';
import {AvFeedback, AvForm, AvGroup, AvInput, AvField} from 'availity-reactstrap-validation';
import {Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';

import {IParticipant} from 'app/shared/model/participant.model';
import {getEntities as getParticipants} from 'app/entities/participant/participant.reducer';
import {ITrack} from 'app/shared/model/track.model';
import {getEntities as getTracks} from 'app/entities/track/track.reducer';
import {getEntity, updateEntity, createEntity, reset} from './team.reducer';
import {ITeam} from 'app/shared/model/team.model';
import {convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime} from 'app/shared/util/date-utils';
import {mapIdList} from 'app/shared/util/entity-utils';

export interface ITeamUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

export const TeamUpdate = (props: ITeamUpdateProps) => {
  const [registratorId, setRegistratorId] = useState('0');
  const [participantId, setParticipantId] = useState('0');
  const [caseId, setCaseId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const {teamEntity, participants, tracks, loading, updating} = props;

  const handleClose = () => {
    props.history.push('/team');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getParticipants();
    props.getTracks();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...teamEntity,
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
          <h2 id="botApp.team.home.createOrEditLabel">
            <Translate contentKey="botApp.team.home.createOrEditLabel">Create or edit a Team</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : teamEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="team-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="team-id" type="text" className="form-control" name="id" required readOnly/>
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="titleLabel" for="team-title">
                  <Translate contentKey="botApp.team.title">Title</Translate>
                </Label>
                <AvField
                  id="team-title"
                  type="text"
                  name="title"
                  validate={{
                    required: {value: true, errorMessage: translate('entity.validation.required')},
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="statusLabel" for="team-status">
                  <Translate contentKey="botApp.team.status">Status</Translate>
                </Label>
                <AvInput
                  id="team-status"
                  type="select"
                  className="form-control"
                  name="status"
                  value={(!isNew && teamEntity.status) || 'ADDED'}
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
                <Label id="commentLabel" for="team-comment">
                  <Translate contentKey="botApp.team.comment">Comment</Translate>
                </Label>
                <AvField id="team-comment" type="text" name="comment"/>
              </AvGroup>
              <AvGroup>
                <Label for="team-registrator">
                  <Translate contentKey="botApp.team.registrator">Registrator</Translate>
                </Label>
                <AvInput id="team-registrator" type="select" className="form-control" name="registrator.id">
                  <option value="" key="0"/>
                  {participants
                    ? participants.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.fullName}
                      </option>
                    ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="team-case">
                  <Translate contentKey="botApp.team.case">Case</Translate>
                </Label>
                <AvInput id="team-case" type="select" className="form-control" name="case.id">
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
              <Button tag={Link} id="cancel-save" to="/team" replace color="info">
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
  participants: storeState.participant.entities,
  tracks: storeState.track.entities,
  teamEntity: storeState.team.entity,
  loading: storeState.team.loading,
  updating: storeState.team.updating,
  updateSuccess: storeState.team.updateSuccess,
});

const mapDispatchToProps = {
  getParticipants,
  getTracks,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TeamUpdate);
