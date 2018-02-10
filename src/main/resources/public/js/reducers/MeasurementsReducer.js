import types from "../actions/types";

const initialState = {
    sources: [],
    measurementsPerSource: {},
    isFetching: false,
    error: null
};

export default function (state = initialState, action) {
    switch (action.type) {
        case types.GET_MEASUREMENT_SOURCES:
            return {
                ...state,
                isFetching: true,
                error: null
            };
        case types.GET_MEASUREMENT_SOURCES_SUCCESS:
            const sources = action.payload.data;
            sources.sort((a, b) => a.name.localeCompare(b.name));

            return {
                ...state,
                isFetching: false,
                error: null,
                sources
            };
        case types.GET_MEASUREMENT_SOURCES_FAIL:
            return {
                ...state,
                isFetching: false,
                error: action.error
            };
        case types.GET_MEASUREMENTS:
            return {
                ...state,
                isFetching: true,
                error: null
            };
        case types.GET_MEASUREMENTS_SUCCESS:
            // TODO: Merge with existing measurements
            const measurements = action.payload.data;
            const measurementsPerSource = state.measurementsPerSource;
            if (measurements.length > 0) {
                const sourceName = measurements[0].source.name;
                measurementsPerSource[sourceName] = measurements;
            }

            return {
                ...state,
                isFetching: false,
                error: null,
                measurementsPerSource
            };
        case types.GET_MEASUREMENTS_FAIL:
            return {
                ...state,
                isFetching: false,
                error: action.error
            };
        default:
            return state;
    }
}
