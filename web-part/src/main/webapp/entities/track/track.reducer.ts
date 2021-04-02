import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ITrack, defaultValue } from 'app/shared/model/track.model';

export const ACTION_TYPES = {
  FETCH_TRACK_LIST: 'track/FETCH_TRACK_LIST',
  FETCH_TRACK: 'track/FETCH_TRACK',
  CREATE_TRACK: 'track/CREATE_TRACK',
  UPDATE_TRACK: 'track/UPDATE_TRACK',
  DELETE_TRACK: 'track/DELETE_TRACK',
  RESET: 'track/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ITrack>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type TrackState = Readonly<typeof initialState>;

// Reducer

export default (state: TrackState = initialState, action): TrackState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_TRACK_LIST):
    case REQUEST(ACTION_TYPES.FETCH_TRACK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_TRACK):
    case REQUEST(ACTION_TYPES.UPDATE_TRACK):
    case REQUEST(ACTION_TYPES.DELETE_TRACK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_TRACK_LIST):
    case FAILURE(ACTION_TYPES.FETCH_TRACK):
    case FAILURE(ACTION_TYPES.CREATE_TRACK):
    case FAILURE(ACTION_TYPES.UPDATE_TRACK):
    case FAILURE(ACTION_TYPES.DELETE_TRACK):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_TRACK_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_TRACK):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_TRACK):
    case SUCCESS(ACTION_TYPES.UPDATE_TRACK):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_TRACK):
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

const apiUrl = 'api/tracks';

// Actions

export const getEntities: ICrudGetAllAction<ITrack> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_TRACK_LIST,
  payload: axios.get<ITrack>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<ITrack> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_TRACK,
    payload: axios.get<ITrack>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<ITrack> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_TRACK,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ITrack> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_TRACK,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ITrack> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_TRACK,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
