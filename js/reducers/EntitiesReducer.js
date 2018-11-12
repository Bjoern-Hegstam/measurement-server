import * as types from "../actions/types";

const initialState = {
    instrumentations: [],
    measurements: {},
};

export default function measurements(state = initialState, action) {
    switch (action.type) {
        case types.GET_INSTRUMENTATIONS_SUCCESS: {
            return {
                ...state,
                instrumentations: action.payload.data,
            };
        }
        case types.GET_MEASUREMENTS_SUCCESS: {
            const { data } = action.payload;
            if (!data) {
                return state;
            }

            const { instrumentationId, sensorId } = data[0];

            return {
                ...state,
                measurements: {
                    ...state.measurements,
                    [instrumentationId]: {
                        ...state.measurements[instrumentationId],
                        [sensorId]: data,
                    },
                }
            };
        }
        default:
            return state;
    }
}
