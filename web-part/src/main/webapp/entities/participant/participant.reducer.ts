import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IParticipant, defaultValue } from 'app/shared/model/participant.model';

export const ACTION_TYPES = {
  FETCH_PARTICIPANT_LIST: 'participant/FETCH_PARTICIPANT_LIST',
  FETCH_PARTICIPANT: 'participant/FETCH_PARTICIPANT',
  CREATE_PARTICIPANT: 'participant/CREATE_PARTICIPANT',
  UPDATE_PARTICIPANT: 'participant/UPDATE_PARTICIPANT',
  DELETE_PARTICIPANT: 'participant/DELETE_PARTICIPANT',
  RESET: 'participant/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IParticipant>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type ParticipantState = Readonly<typeof initialState>;

// Reducer

export default (state: ParticipantState = initialState, action): ParticipantState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_PARTICIPANT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_PARTICIPANT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_PARTICIPANT):
    case REQUEST(ACTION_TYPES.UPDATE_PARTICIPANT):
    case REQUEST(ACTION_TYPES.DELETE_PARTICIPANT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_PARTICIPANT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_PARTICIPANT):
    case FAILURE(ACTION_TYPES.CREATE_PARTICIPANT):
    case FAILURE(ACTION_TYPES.UPDATE_PARTICIPANT):
    case FAILURE(ACTION_TYPES.DELETE_PARTICIPANT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_PARTICIPANT_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_PARTICIPANT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_PARTICIPANT):
    case SUCCESS(ACTION_TYPES.UPDATE_PARTICIPANT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_PARTICIPANT):
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

const apiUrl = 'api/participants';

// Actions

export const getEntities: ICrudGetAllAction<IParticipant> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_PARTICIPANT_LIST,
  payload: axios.get<IParticipant>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IParticipant> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_PARTICIPANT,
    payload: axios.get<IParticipant>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IParticipant> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_PARTICIPANT,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IParticipant> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_PARTICIPANT,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IParticipant> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_PARTICIPANT,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
