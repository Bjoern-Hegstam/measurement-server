import PropTypes from 'prop-types';

const SensorType = PropTypes.shape({
    id: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
});

export const InstrumentationBodyType = {
    id: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    sensorRegistrations: PropTypes.shape({
        sensor: SensorType.isRequired,
        validFromUtc: PropTypes.string.isRequired,
        validToUtc: PropTypes.string,
    }).isRequired,
};

export const InstrumentationType = PropTypes.shape(InstrumentationBodyType);

export const MeasurementType = PropTypes.shape({
    instrumentationId: PropTypes.string.isRequired,
    sensorId: PropTypes.string.isRequired,
    createdAtMillis: PropTypes.number,
    type: PropTypes.string,
    value: PropTypes.number,
    unit: PropTypes.string
});
