import * as types from "./types";

export function getInstrumentations() {
    return {
        type: types.GET_INSTRUMENTATIONS,
        payload: {
            request: {
                method: 'get',
                url: '/instrumentation',
                responseType: 'json',
            }
        }
    }
}

export function getMeasurements({ instrumentationId, sensorId, perPage = 80, page = 1 }) {
    return {
        type: types.GET_MEASUREMENTS,
        payload: {
            request: {
                method: 'get',
                url: `/instrumentation/${instrumentationId}/sensor/${sensorId}/measurement?per_page=${perPage}&page=${page}`,
                responseType: 'json'
            }
        }
    }
}