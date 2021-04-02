import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {RouteComponentProps} from 'react-router-dom';
import {Modal, ModalHeader, ModalBody, ModalFooter, Button} from 'reactstrap';
import {Translate, ICrudGetAction, ICrudDeleteAction} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {ITeam} from 'app/shared/model/team.model';
import {IRootState} from 'app/shared/reducers';
import {getEntity, deleteEntity} from './team.reducer';

export interface ITeamDeleteDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

export const TeamDeleteDialog = (props: ITeamDeleteDialogProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const handleClose = () => {
    props.history.push('/team');
  };

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const confirmDelete = () => {
    props.deleteEntity(props.teamEntity.id);
  };

  const {teamEntity} = props;
  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose}>
        <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="botApp.team.delete.question">
        <Translate contentKey="botApp.team.delete.question" interpolate={{id: teamEntity.id}}>
          Are you sure you want to delete this Team?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban"/>
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-delete-team" color="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash"/>
          &nbsp;
          <Translate contentKey="entity.action.delete">Delete</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapStateToProps = ({team}: IRootState) => ({
  teamEntity: team.entity,
  updateSuccess: team.updateSuccess,
});

const mapDispatchToProps = {getEntity, deleteEntity};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TeamDeleteDialog);
