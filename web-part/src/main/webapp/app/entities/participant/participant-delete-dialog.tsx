import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {RouteComponentProps} from 'react-router-dom';
import {Modal, ModalHeader, ModalBody, ModalFooter, Button} from 'reactstrap';
import {Translate, ICrudGetAction, ICrudDeleteAction} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IParticipant} from 'app/shared/model/participant.model';
import {IRootState} from 'app/shared/reducers';
import {getEntity, deleteEntity} from './participant.reducer';

export interface IParticipantDeleteDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

export const ParticipantDeleteDialog = (props: IParticipantDeleteDialogProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const handleClose = () => {
    props.history.push('/participant');
  };

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const confirmDelete = () => {
    props.deleteEntity(props.participantEntity.id);
  };

  const {participantEntity} = props;
  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose}>
        <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="botApp.participant.delete.question">
        <Translate contentKey="botApp.participant.delete.question" interpolate={{id: participantEntity.id}}>
          Are you sure you want to delete this Participant?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban"/>
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-delete-participant" color="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash"/>
          &nbsp;
          <Translate contentKey="entity.action.delete">Delete</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapStateToProps = ({participant}: IRootState) => ({
  participantEntity: participant.entity,
  updateSuccess: participant.updateSuccess,
});

const mapDispatchToProps = {getEntity, deleteEntity};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ParticipantDeleteDialog);
