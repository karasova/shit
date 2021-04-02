import React, {useState, useEffect} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row, Table} from 'reactstrap';
import {Translate, ICrudGetAllAction} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities} from './track.reducer';
import {ITrack} from 'app/shared/model/track.model';
import {APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT} from 'app/config/constants';

export interface ITrackProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {
}

export const Track = (props: ITrackProps) => {
  useEffect(() => {
    props.getEntities();
  }, []);

  const {trackList, match, loading} = props;
  return (
    <div>
      <h2 id="track-heading">
        <Translate contentKey="botApp.track.home.title">Tracks</Translate>
        <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
          <FontAwesomeIcon icon="plus"/>
          &nbsp;
          <Translate contentKey="botApp.track.home.createLabel">Create new Track</Translate>
        </Link>
      </h2>
      <div className="table-responsive">
        {trackList && trackList.length > 0 ? (
          <Table responsive>
            <thead>
            <tr>
              <th>
                <Translate contentKey="global.field.id">ID</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.track.title">Title</Translate>
              </th>
              <th>
                <Translate contentKey="botApp.track.count">Participants Count</Translate>
              </th>
              <th/>
            </tr>
            </thead>
            <tbody>
            {trackList.map((track, i) => (
              <tr key={`entity-${i}`}>
                <td>
                  <Button tag={Link} to={`${match.url}/${track.id}`} color="link" size="sm">
                    {track.id}
                  </Button>
                </td>
                <td>{track.title}</td>
                <td>{track.derived ? track.derived.count + '/' + track.derived.max : ''}</td>
                <td className="text-right">
                  <div className="btn-group flex-btn-group-container">
                    <Button tag={Link} to={`${match.url}/${track.id}`} color="info" size="sm">
                      <FontAwesomeIcon icon="eye"/>{' '}
                      <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                    </Button>
                    <Button tag={Link} to={`${match.url}/${track.id}/edit`} color="primary" size="sm">
                      <FontAwesomeIcon icon="pencil-alt"/>{' '}
                      <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                    </Button>
                    <Button tag={Link} to={`${match.url}/${track.id}/delete`} color="danger" size="sm">
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
              <Translate contentKey="botApp.track.home.notFound">No Tracks found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({track}: IRootState) => ({
  trackList: track.entities,
  loading: track.loading,
});

const mapDispatchToProps = {
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Track);
