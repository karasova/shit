import React, {useState, useEffect} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row, Table} from 'reactstrap';
import {Translate, ICrudGetAllAction} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities} from './participant.reducer';
import {IParticipant} from 'app/shared/model/participant.model';
import {APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT} from 'app/config/constants';

export interface IParticipantProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {
}

export const Participant = (props: IParticipantProps) => {
  useEffect(() => {
    props.getEntities();
  }, []);

  const {participantList, match, loading} = props;
  return (
    <div>
      <h2 id="participant-heading">
        <Translate contentKey="botApp.participant.home.title">Participants</Translate>
        <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
          <FontAwesomeIcon icon="plus"/>
          &nbsp;
          <Translate contentKey="botApp.participant.home.createLabel">Create new Participant</Translate>
        </Link>
      </h2>
      <div className="table-responsive">
        {participantList && participantList.length > 0 ? (
          <Table responsive>
            <thead>
            <tr>
              <th>
                <Translate contentKey="global.field.id">ID</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.participant.vkId">Vk Id</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.participant.fullName">Full Name</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.participant.age">Age</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.participant.employer">Employer</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.participant.phoneNumber">Phone Number</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.participant.team">Team</Translate>
              </th>
              <th/>
            </tr>
            </thead>
            <tbody>
            {participantList.map((participant, i) => (
              <tr key={`entity-${i}`}>
                <td>
                  <Button tag={Link} to={`${match.url}/${participant.id}`} color="link" size="sm">
                    {participant.id}
                  </Button>
                </td>
                <td>{participant.vkId}</td>
                <td>{participant.fullName}</td>
                <td>{participant.age}</td>
                <td>{participant.employer}</td>
                <td>{participant.phoneNumber}</td>
                <td>{participant.team ?
                  <Link to={`team/${participant.team.id}`}>{participant.team.title}</Link> : ''}</td>
                <td className="text-right">
                  <div className="btn-group flex-btn-group-container">
                    <Button tag={Link} to={`${match.url}/${participant.id}`} color="info" size="sm">
                      <FontAwesomeIcon icon="eye"/>{' '}
                      <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                    </Button>
                    <Button tag={Link} to={`${match.url}/${participant.id}/edit`} color="primary" size="sm">
                      <FontAwesomeIcon icon="pencil-alt"/>{' '}
                      <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                    </Button>
                    <Button tag={Link} to={`${match.url}/${participant.id}/delete`} color="danger" size="sm">
                      <FontAwesomeIcon icon="trash"/>{' '}
                      <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="botApp.participant.home.notFound">No Participants found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({participant}: IRootState) => ({
  participantList: participant.entities,
  loading: participant.loading,
});

const mapDispatchToProps = {
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Participant);
