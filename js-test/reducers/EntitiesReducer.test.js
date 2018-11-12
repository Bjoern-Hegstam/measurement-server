import reducer from '../../js/reducers/EntitiesReducer';
import * as types from '../../js/actions/types';

const initialState = {
    instrumentations: [],
    measurements: {},
};

it('GET_MEASUREMENTS_SUCCESS', () => {
    // given
    const instrumentationId = "33749968-00a8-493c-4817-8425602710cc";
    const sensorId = "ae125559-f360-5bc3-16cc-9b662ee09c26";
    const action = {
        type: types.GET_MEASUREMENTS_SUCCESS,
        payload: {
            data: [
                {
                    instrumentationId,
                    sensorId,
                    createdAtMillis: "1518111688248",
                    type: "soil_moisture",
                    unit: "unit",
                    value: 774,
                }
            ]
        }
    };

    // when
    const newState = reducer(initialState, action);

    // then
    expect(newState.measurements[instrumentationId][sensorId]).toEqual([
        {
            instrumentationId,
            sensorId,
            createdAtMillis: "1518111688248",
            type: "soil_moisture",
            unit: "unit",
            value: 774,
        }
    ]);
});