import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IMailingTask, defaultValue } from 'app/shared/model/mailing-task.model';

export const ACTION_TYPES = {
  FETCH_MAILINGTASK_LIST: 'mailingTask/FETCH_MAILINGTASK_LIST',
  FETCH_MAILINGTASK: 'mailingTask/FETCH_MAILINGTASK',
  CREATE_MAILINGTASK: 'mailingTask/CREATE_MAILINGTASK',
  UPDATE_MAILINGTASK: 'mailingTask/UPDATE_MAILINGTASK',
  DELETE_MAILINGTASK: 'mailingTask/DELETE_MAILINGTASK',
  RESET: 'mailingTask/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IMailingTask>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type MailingTaskState = Readonly<typeof initialState>;

// Reducer

export default (state: MailingTaskState = initialState, action): MailingTaskState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_MAILINGTASK_LIST):
    case REQUEST(ACTION_TYPES.FETCH_MAILINGTASK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_MAILINGTASK):
    case REQUEST(ACTION_TYPES.UPDATE_MAILINGTASK):
    case REQUEST(ACTION_TYPES.DELETE_MAILINGTASK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_MAILINGTASK_LIST):
    case FAILURE(ACTION_TYPES.FETCH_MAILINGTASK):
    case FAILURE(ACTION_TYPES.CREATE_MAILINGTASK):
    case FAILURE(ACTION_TYPES.UPDATE_MAILINGTASK):
    case FAILURE(ACTION_TYPES.DELETE_MAILINGTASK):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_MAILINGTASK_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_MAILINGTASK):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_MAILINGTASK):
    case SUCCESS(ACTION_TYPES.UPDATE_MAILINGTASK):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_MAILINGTASK):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/mailing-tasks';

// Actions

export const getEntities: ICrudGetAllAction<IMailingTask> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_MAILINGTASK_LIST,
  payload: axios.get<IMailingTask>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IMailingTask> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_MAILINGTASK,
    payload: axios.get<IMailingTask>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IMailingTask> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_MAILINGTASK,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IMailingTask> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_MAILINGTASK,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IMailingTask> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_MAILINGTASK,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
