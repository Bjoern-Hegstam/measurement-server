import types from "../actions/types";

const initialState = {
    sensors: [],
    isFetching: false,
    error: null
};

export default function (state = initialState, action) {
    console.log(action);
    switch (action.type) {
        case types.GET_MEASUREMENT_SOURCES:
            return {
                ...state,
                isFetching: true
            };
        case types.GET_MEASUREMENT_SOURCES_SUCCESS:
            return {
                ...state,
                isFetching: false,
                sensors: action.payload.data
            };
        case types.GET_MEASUREMENT_SOURCES_FAIL:
            return {
                ...state,
                isFetching: false,
                error: action.error
            };
        default:
            return state;
    }
}
