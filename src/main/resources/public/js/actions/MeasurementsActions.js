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

export function getMeasurements(sourceName) {
    return {
        type: types.GET_MEASUREMENTS,
        payload: {
            request: {
                method: 'get',
                url: `/sources/${sourceName}/measurements`,
                responseType: 'json'
            }
        }
    }
}