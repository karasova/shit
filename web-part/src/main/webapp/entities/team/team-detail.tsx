import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Row, Col} from 'reactstrap';
import {Translate, ICrudGetAction} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './team.reducer';
import {ITeam} from 'app/shared/model/team.model';
import {APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT} from 'app/config/constants';

export interface ITeamDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

export const TeamDetail = (props: ITeamDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const {teamEntity} = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          <Translate contentKey="botApp.team.detail.title">Team</Translate> [<b>{teamEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="title">
              <Translate contentKey="botApp.team.title">Title</Translate>
            </span>
          </dt>
          <dd>{teamEntity.title}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="botApp.team.status">Status</Translate>
            </span>
          </dt>
          <dd>{teamEntity.status}</dd>
          <dt>
            <span id="comment">
              <Translate contentKey="botApp.team.comment">Comment</Translate>
            </span>
          </dt>
          <dd>{teamEntity.comment}</dd>
          <dt>
            <Translate contentKey="botApp.team.registrator">Registrator</Translate>
          </dt>
          <dd>{teamEntity.registrator ? teamEntity.registrator.fullName : ''}</dd>
          <dt>
            <Translate contentKey="botApp.team.case">Case</Translate>
          </dt>
          <dd>{teamEntity.case ? teamEntity.case.title : (
            <span className="badge badge-danger">КЕЙС НЕ ВЫБРАН</span>
          )}</dd>
        </dl>
        <Button tag={Link} to="/team" replace color="info">
          <FontAwesomeIcon icon="arrow-left"/>{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/team/${teamEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt"/>{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({team}: IRootState) => ({
  teamEntity: team.entity,
});

const mapDispatchToProps = {getEntity};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TeamDetail);
