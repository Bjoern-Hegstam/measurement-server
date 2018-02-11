import {combineReducers} from 'redux';
import sources from './MeasurementSourcesReducer';
import measurementsBySource from './MeasurementsReducer';

export default combineReducers({
    sources,
    measurementsBySource
});