import React, {useState, useEffect} from 'react';
import {connect} from 'react-redux';
import {Redirect, RouteComponentProps} from 'react-router-dom';

import {IRootState} from 'app/shared/reducers';
import {upload} from 'app/shared/reducers/team-upload';
import TeamImportModal from "./team-import-modal";

export interface ITeamImportProps extends StateProps, DispatchProps, RouteComponentProps<{}> {
}

export const TeamImport = (props: ITeamImportProps) => {
  const [showModal, setShowModal] = useState(props.showModal);

  useEffect(() => {
    setShowModal(true);
  }, []);

  const handleUpload = (file) => props.upload(file);

  const handleClose = () => {
    setShowModal(false);
    props.history.push("/team")
  };

  return <TeamImportModal showModal={showModal} handleUpload={handleUpload} handleClose={handleClose}
                          uploadError={props.uploadError} uploadSuccess={props.uploadSuccess}/>;
};

const mapStateToProps = ({teamUpload}: IRootState) => ({
  uploadError: teamUpload.uploadError,
  showModal: teamUpload.showModalUpload,
  uploadSuccess: teamUpload.uploadSuccess
});

const mapDispatchToProps = {upload};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TeamImport);
