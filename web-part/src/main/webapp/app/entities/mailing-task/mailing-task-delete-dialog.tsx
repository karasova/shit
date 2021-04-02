import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {RouteComponentProps} from 'react-router-dom';
import {Modal, ModalHeader, ModalBody, ModalFooter, Button} from 'reactstrap';
import {Translate, ICrudGetAction, ICrudDeleteAction} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IMailingTask} from 'app/shared/model/mailing-task.model';
import {IRootState} from 'app/shared/reducers';
import {getEntity, deleteEntity} from './mailing-task.reducer';

export interface IMailingTaskDeleteDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

export const MailingTaskDeleteDialog = (props: IMailingTaskDeleteDialogProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const handleClose = () => {
    props.history.push('/mailing-task');
  };

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const confirmDelete = () => {
    props.deleteEntity(props.mailingTaskEntity.id);
  };

  const {mailingTaskEntity} = props;
  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose}>
        <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="botApp.mailingTask.delete.question">
        <Translate contentKey="botApp.mailingTask.delete.question" interpolate={{id: mailingTaskEntity.id}}>
          Are you sure you want to delete this MailingTask?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban"/>
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-delete-mailingTask" color="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash"/>
          &nbsp;
          <Translate contentKey="entity.action.delete">Delete</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapStateToProps = ({mailingTask}: IRootState) => ({
  mailingTaskEntity: mailingTask.entity,
  updateSuccess: mailingTask.updateSuccess,
});

const mapDispatchToProps = {getEntity, deleteEntity};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(MailingTaskDeleteDialog);
