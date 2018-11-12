import { combineReducers } from 'redux';
import entities from './EntitiesReducer';
import loading from './LoadingReducer';
import error from './ErrorReducer';

export default combineReducers({
    entities,
    request: combineReducers({
        loading,
        error,
    }),
});