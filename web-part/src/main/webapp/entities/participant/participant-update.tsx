import React, {useState, useEffect} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Row, Col, Label} from 'reactstrap';
import {AvFeedback, AvForm, AvGroup, AvInput, AvField} from 'availity-reactstrap-validation';
import {Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';

import {ITeam} from 'app/shared/model/team.model';
import {getEntities as getTeams} from 'app/entities/team/team.reducer';
import {getEntity, updateEntity, createEntity, reset} from './participant.reducer';
import {IParticipant} from 'app/shared/model/participant.model';
import {convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime} from 'app/shared/util/date-utils';
import {mapIdList} from 'app/shared/util/entity-utils';

export interface IParticipantUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

export const ParticipantUpdate = (props: IParticipantUpdateProps) => {
  const [teamId, setTeamId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const {participantEntity, teams, loading, updating} = props;

  const handleClose = () => {
    props.history.push('/participant');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getTeams();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...participantEntity,
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
          <h2 id="botApp.participant.home.createOrEditLabel">
            <Translate contentKey="botApp.participant.home.createOrEditLabel">Create or edit a Participant</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : participantEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="participant-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="participant-id" type="text" className="form-control" name="id" required readOnly/>
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="vkIdLabel" for="participant-vkId">
                  <Translate contentKey="botApp.participant.vkId">Vk Id</Translate>
                </Label>
                <AvField id="participant-vkId" type="string" className="form-control" name="vkId"/>
              </AvGroup>
              <AvGroup>
                <Label id="fullNameLabel" for="participant-fullName">
                  <Translate contentKey="botApp.participant.fullName">Full Name</Translate>
                </Label>
                <AvField id="participant-fullName" type="text" name="fullName"/>
              </AvGroup>
              <AvGroup>
                <Label id="ageLabel" for="participant-age">
                  <Translate contentKey="botApp.participant.age">Age</Translate>
                </Label>
                <AvField id="participant-age" type="string" className="form-control" name="age"/>
              </AvGroup>
              <AvGroup>
                <Label id="employerLabel" for="participant-employer">
                  <Translate contentKey="botApp.participant.employer">Employer</Translate>
                </Label>
                <AvField id="participant-employer" type="text" name="employer"/>
              </AvGroup>
              <AvGroup>
                <Label id="phoneNumberLabel" for="participant-phoneNumber">
                  <Translate contentKey="botApp.participant.phoneNumber">Phone Number</Translate>
                </Label>
                <AvField id="participant-phoneNumber" type="text" name="phoneNumber"/>
              </AvGroup>
              <AvGroup>
                <Label for="participant-team">
                  <Translate contentKey="botApp.participant.team">Team</Translate>
                </Label>
                <AvInput id="participant-team" type="select" className="form-control" name="team.id">
                  <option value="" key="0"/>
                  {teams
                    ? teams.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/participant" replace color="info">
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
  teams: storeState.team.entities,
  participantEntity: storeState.participant.entity,
  loading: storeState.participant.loading,
  updating: storeState.participant.updating,
  updateSuccess: storeState.participant.updateSuccess,
});

const mapDispatchToProps = {
  getTeams,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ParticipantUpdate);
