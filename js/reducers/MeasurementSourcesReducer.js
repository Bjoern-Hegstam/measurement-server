import types from "../actions/types";

export default function (state = [], action) {
    switch (action.type) {
        case types.GET_MEASUREMENT_SOURCES:
            return state;
        case types.GET_MEASUREMENT_SOURCES_SUCCESS:
            const sources = action.payload.data;
            sources.sort((a, b) => a.name.localeCompare(b.name));

            return sources;
        default:
            return state;
    }
}
