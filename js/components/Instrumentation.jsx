import React from "react";
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { getMeasurements } from "../actions/MeasurementsActions";
import MeasurementsGraph from './MeasurementsGraph';
import { InstrumentationBodyType, MeasurementType } from "../types";
import { createErrorSelector, createLoadingSelector, getMeasurementsSelector } from '../selectors';
import * as types from '../actions/types';
import moment from 'moment';

class Instrumentation extends React.Component {
    static propTypes = {
        ...InstrumentationBodyType,

        measurementsPerSensor: PropTypes.objectOf(MeasurementType).isRequired,

        getMeasurements: PropTypes.func.isRequired,
        fetchingMeasurements: PropTypes.bool.isRequired,
        errorGetMeasurements: PropTypes.object,
    };

    static defaultProps = {
        measurementsPerSensor: {},
    };

    componentDidMount() {
        this.getSensors()
            .forEach(sensor => this.props.getMeasurements({
                instrumentationId: this.props.id,
                sensorId: sensor.id,
            }));
    }

    getSensors() {
        const { sensorRegistrations } = this.props;

        const now = moment();

        const sensors = sensorRegistrations
            .filter(sr => moment(sr.validFromUtc).isBefore(now) && (!sr.validToUtc || moment(sr.validToUtc).isAfter(now)))
            .map(sr => sr.sensor);

        sensors.sort((s1, s2) => s1.name.localeCompare(s2.name));

        return sensors;
    }

    getMeasurements(sensor) {
        return this
            .props
            .measurementsPerSensor[sensor.id] || [];
    }

    render() {
        return (
            <div>
                {this.getSensors().map(sensor => this.renderGraph(sensor))}
            </div>
        )
    }

    renderGraph(sensor) {
        return (
            <>
                <p className="source-header">{sensor.name}</p>
                <MeasurementsGraph measurements={this.getMeasurements(sensor)} />
            </>
        )
    }
}

const mapStateToProps = (state, ownProps) => ({
    measurementsPerSensor: getMeasurementsSelector(state, ownProps.id),

    fetchingMeasurements: createLoadingSelector(types.GET_MEASUREMENTS)(state),
    errorGetMeasurements: createErrorSelector(types.GET_MEASUREMENTS)(state),
});

const mapDispatchToProps = (dispatch) => ({
    getMeasurements: (args) => dispatch(getMeasurements(args)),
});

export default connect(mapStateToProps, mapDispatchToProps)(Instrumentation);