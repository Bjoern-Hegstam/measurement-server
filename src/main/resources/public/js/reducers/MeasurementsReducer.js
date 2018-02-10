import types from "../actions/types";

function measurements(state = {
    isFetching: true,
    error: null,
    paginationInformation: {
        total: null,
        totalPage: null,
        page: null,
        perPage: null,
        prevPage: null,
        nextPage: null
    },
    latestTimestamp: null,
    data: []
}, action) {
    switch (action) {
        case types.GET_MEASUREMENTS:
            return {
                ...state,
                isFetching: true,
                error: null
            };
        case types.GET_MEASUREMENTS_SUCCESS:
            const data = action.payload.data;
            let latestTimestamp = null;
            if (data.length > 0) {
                latestTimestamp = data[data.length - 1].createdAtMillis;
            }


            return {
                ...state,
                isFetching: false,
                error: null,
                // TODO: Read pagination information from header
                latestTimestamp,
                data
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

export default function (state = {}, action) {
    switch (action.type) {
        case types.GET_MEASUREMENTS:
        case types.GET_MEASUREMENTS_SUCCESS:
        case types.GET_MEASUREMENTS_FAIL:
            const sourceName = 'queryInfo' in action ?
                action.queryInfo.sourceName :
                action.meta.previousAction.queryInfo.sourceName;

            return {
                ...state,
                [sourceName]: measurements(state[sourceName], action)
            };
        default:
            return state;
    }
}
