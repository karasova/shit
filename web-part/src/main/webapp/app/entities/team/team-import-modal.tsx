import React from 'react';
import {Translate, translate} from 'react-jhipster';
import {Input, Button, Modal, ModalHeader, ModalBody, ModalFooter, Label, Alert, Row, Col} from 'reactstrap';
import {AvForm, AvField, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Link} from 'react-router-dom';

export interface ILoginModalProps {
  uploadError: boolean;
  showModal: boolean;
  handleUpload: Function;
  handleClose: Function;
  uploadSuccess: boolean;
}

class TeamImportModal extends React.Component<ILoginModalProps> {
  csvFile: any;

  handleSubmit = (event, errors, {file}) => {
    const {handleUpload} = this.props;
    handleUpload(this.csvFile.files[0]);
  };

  render() {
    const {handleClose, uploadError, uploadSuccess} = this.props;

    return (
      <Modal isOpen={this.props.showModal} toggle={handleClose} backdrop="static" id="login-page" autoFocus={false}>
        <AvForm onSubmit={this.handleSubmit}>
          <ModalHeader id="team-import-title" toggle={handleClose}>
            <Translate contentKey="botApp.team-import.title">Team import</Translate>
          </ModalHeader>
          <ModalBody>
            <Row>
              <Col md="12">
                {uploadError ? (
                  <Alert color="danger">
                    <strong>Upload error!</strong>
                  </Alert>
                ) : null}
              </Col>
              <Col md="12">
                <input
                  name="file"
                  type="file"
                  ref={(ref) => {
                    this.csvFile = ref
                  }}
                  required
                />
              </Col>
            </Row>
            <div className="mt-1">&nbsp;</div>

            {uploadSuccess ? (
              <Alert color="success">
                <Translate contentKey="botApp.team-import.success">Upload successful!</Translate>
              </Alert>
            ) : (
              <Alert color="warning">
                <Translate contentKey="botApp.team-import.file-warning">Please, dont forget to delete two first lines in
                  the file</Translate>
              </Alert>
            )}
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={handleClose} tabIndex="1">
              <Translate contentKey="entity.action.cancel">Cancel</Translate>
            </Button>{' '}
            {uploadSuccess ? (
              <Button color="primary" onClick={handleClose}>
                <Translate contentKey="botApp.team-import.form.close">Close</Translate>
              </Button>
            ) : (
              <Button color="primary" type="submit">
                <Translate contentKey="botApp.team-import.form.button">Import</Translate>
              </Button>
            )}
          </ModalFooter>
        </AvForm>
      </Modal>
    );
  }
}

export default TeamImportModal;
