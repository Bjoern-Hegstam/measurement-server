export const createLoadingSelector = (...actionTypes) => state => actionTypes
    .some(type => state.request.loading[type]);

export const createErrorSelector = (...actionTypes) => state => actionTypes
    .find(type => state.request.error[type]) || null;

export function getInstrumentationsSelector(state) {
    return state.entities.instrumentations;
}

export function getMeasurementsSelector(state, instrumentationId) {
    return state
        .entities
        .measurements[instrumentationId] || [];
}