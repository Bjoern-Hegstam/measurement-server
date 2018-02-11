import types from "./types";

export function getMeasurementSources() {
    return {
        type: types.GET_MEASUREMENT_SOURCES,
        payload: {
            request: {
                method: 'get',
                url: '/sources',
                responseType: 'json'
            }
        }
    }
}

export function getMeasurements(sourceName, perPage=100, page=1) {
    return {
        type: types.GET_MEASUREMENTS,
        queryInfo: {
            sourceName
        },
        payload: {
            request: {
                method: 'get',
                url: `/sources/${sourceName}/measurements?per_page=${perPage}&page=${page}`,
                responseType: 'json'
            }
        }
    }
}