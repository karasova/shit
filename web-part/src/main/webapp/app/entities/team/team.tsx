import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Table} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {TeamStatus} from "app/shared/model/enumerations/team-status.model";
import {IRootState} from 'app/shared/reducers';
import {getEntities} from './team.reducer';

export interface ITeamProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {
}

export const Team = (props: ITeamProps) => {
  useEffect(() => {
    props.getEntities();
  }, []);

  const statusBadge = new Map<TeamStatus, string>([
    [TeamStatus.ADDED, 'light'],
    [TeamStatus.CANCELED, 'dark'],
    [TeamStatus.CASE_SELECTION, 'warning'],
    [TeamStatus.CASE_SELECTED, 'primary'],
    [TeamStatus.PARTICIPANTS_NEEDED, 'dark'],
    [TeamStatus.REGISTRATION, 'warning'],
    [TeamStatus.REGISTERED, 'success'],
  ]);

  const {teamList, match, loading} = props;
  return (
    <div>
      <h2 id="team-heading">
        <Translate contentKey="botApp.team.home.title">Teams</Translate>
        <div className="float-right">
          <Link to={`${match.url}/import`} className="btn btn-success" id="jh-create-entity">
            <FontAwesomeIcon icon="plus"/>
            &nbsp;
            <Translate contentKey="botApp.team.home.importLabel">Import Teams From CSV</Translate>
          </Link>
          &nbsp;
          <Link to={`${match.url}/new`} className="btn btn-primary" id="jh-create-entity">
            <FontAwesomeIcon icon="plus"/>
            &nbsp;
            <Translate contentKey="botApp.team.home.createLabel">Create new Team</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {teamList && teamList.length > 0 ? (
          <Table responsive>
            <thead>
            <tr>
              <th>
                <Translate contentKey="global.field.id">ID</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.team.title">Title</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.team.status">Status</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.team.comment">Comment</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.team.registrator">Registrator</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.team.teamMembers">Team members</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.team.case">Case</Translate>
              </th>
              <th/>
            </tr>
            </thead>
            <tbody>
            {teamList.map((team, i) => (
              <tr key={`entity-${i}`}>
                <td>
                  <Button tag={Link} to={`${match.url}/${team.id}`} color="link" size="sm">
                    {team.id}
                  </Button>
                </td>
                <td>{team.title}</td>
                <td>
                  <span className={"badge badge-" + statusBadge.get(team.status)}>
                    <Translate contentKey={`botApp.TeamStatus.${team.status}`}/>
                  </span>
                </td>
                <td>{team.comment}</td>
                <td>{team.registrator ?
                  <Link to={`participant/${team.registrator.id}`}>{team.registrator.fullName}</Link> : ''}</td>
                <td>
                  <ul>
                    {team.participants ? team.participants.map((man) => (
                      <li key={man.id}>
                        {man.vkId ? (
                          <a target="_blank" rel="noopener noreferrer"
                             href={"//vk.com/gim198966864?sel=" + man.vkId}>{man.fullName}</a>
                        ) : man.fullName}
                      </li>
                    )) : ''}
                  </ul>
                </td>
                <td>{team.case ? <Link to={`track/${team.case.id}`}>{team.case.title}</Link> : ''}</td>
                <td className="text-right">
                  <div className="btn-group flex-btn-group-container">
                    <Button tag={Link} to={`${match.url}/${team.id}`} color="info" size="sm">
                      <FontAwesomeIcon icon="eye"/>{' '}
                      <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                    </Button>
                    <Button tag={Link} to={`${match.url}/${team.id}/edit`} color="primary" size="sm">
                      <FontAwesomeIcon icon="pencil-alt"/>{' '}
                      <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                    </Button>
                    <Button tag={Link} to={`${match.url}/${team.id}/delete`} color="danger" size="sm">
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
              <Translate contentKey="botApp.team.home.notFound">No Teams found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({team}: IRootState) => ({
  teamList: team.entities,
  loading: team.loading,
});

const mapDispatchToProps = {
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Team);
