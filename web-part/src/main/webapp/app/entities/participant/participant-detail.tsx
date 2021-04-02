import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Row, Col} from 'reactstrap';
import {Translate, ICrudGetAction} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './participant.reducer';
import {IParticipant} from 'app/shared/model/participant.model';
import {APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT} from 'app/config/constants';

export interface IParticipantDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

export const ParticipantDetail = (props: IParticipantDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const {participantEntity} = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          <Translate contentKey="botApp.participant.detail.title">Participant</Translate> [<b>{participantEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="vkId">
              <Translate contentKey="botApp.participant.vkId">Vk Id</Translate>
            </span>
          </dt>
          <dd>{
            participantEntity.vkId ? (
              <a target="_blank" rel="noopener noreferrer"
                 href={"//vk.com/id" + participantEntity.vkId}>{participantEntity.vkId}</a>
            ) : ''
          }</dd>
          <dt>
            <span id="fullName">
              <Translate contentKey="botApp.participant.fullName">Full Name</Translate>
            </span>
          </dt>
          <dd>{participantEntity.fullName}</dd>
          <dt>
            <span id="age">
              <Translate contentKey="botApp.participant.age">Age</Translate>
            </span>
          </dt>
          <dd>{participantEntity.age}</dd>
          <dt>
            <span id="employer">
              <Translate contentKey="botApp.participant.employer">Employer</Translate>
            </span>
          </dt>
          <dd>{participantEntity.employer}</dd>
          <dt>
            <span id="phoneNumber">
              <Translate contentKey="botApp.participant.phoneNumber">Phone Number</Translate>
            </span>
          </dt>
          <dd>{participantEntity.phoneNumber}</dd>
          <dt>
            <Translate contentKey="botApp.participant.team">Team</Translate>
          </dt>
          <dd>{participantEntity.team ? participantEntity.team.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/participant" replace color="info">
          <FontAwesomeIcon icon="arrow-left"/>{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/participant/${participantEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt"/>{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({participant}: IRootState) => ({
  participantEntity: participant.entity,
});

const mapDispatchToProps = {getEntity};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ParticipantDetail);
