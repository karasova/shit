import axios from 'axios';
import { Storage } from 'react-jhipster';

import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';
import { setLocale } from 'app/shared/reducers/locale';

export const ACTION_TYPES = {
  UPLOAD: 'team-upload/UPLOAD',
  ERROR_MESSAGE: 'team-upload/ERROR_MESSAGE',
};

const initialState = {
  loading: false,
  uploadSuccess: false,
  uploadError: false, // Errors returned from server side
  showModalUpload: false,
  errorMessage: (null as unknown) as string, // Errors returned from server side
  redirectMessage: (null as unknown) as string,
};

export type TeamUploadState = Readonly<typeof initialState>;

// Reducer

export default (state: TeamUploadState = initialState, action): TeamUploadState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.UPLOAD):
      return {
        ...state,
        loading: true,
      };
    case FAILURE(ACTION_TYPES.UPLOAD):
      return {
        ...initialState,
        errorMessage: action.payload,
        showModalUpload: true,
        uploadError: true,
      };
    case SUCCESS(ACTION_TYPES.UPLOAD):
      return {
        ...state,
        loading: false,
        uploadError: false,
        showModalUpload: false,
        uploadSuccess: true,
      };
    case ACTION_TYPES.ERROR_MESSAGE:
      return {
        ...initialState,
        showModalUpload: true,
        redirectMessage: action.message,
      };
    default:
      return state;
  }
};

export const displayUploadError = message => ({ type: ACTION_TYPES.ERROR_MESSAGE, message });

export const upload: (file: File) => void = file => async (dispatch, getState) => {
  const formData = new FormData();
  formData.append('file', file);
  await dispatch({
    type: ACTION_TYPES.UPLOAD,
    payload: axios.post('api/teamsupload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }),
  });
};
