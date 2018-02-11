import PropTypes from 'prop-types';

export const MeasurementType = PropTypes.shape({
    source: PropTypes.shape({
        name: PropTypes.string
    }),
    createdAtMillis: PropTypes.number,
    type: PropTypes.string,
    value: PropTypes.number,
    unit: PropTypes.string
});
