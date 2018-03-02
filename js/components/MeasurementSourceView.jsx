import React from "react";
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {getMeasurements} from "../actions/MeasurementsActions";
import MeasurementsGraph from './MeasurementsGraph';
import {MeasurementType} from "../types";

class MeasurementSourceView extends React.Component {
    static propTypes = {
        sourceName: PropTypes.string.isRequired,
        getMeasurements: PropTypes.func.isRequired,
        measurements: PropTypes.arrayOf(MeasurementType)
    };

    static defaultProps = {
        measurements: []
    };

    componentDidMount() {
        this.props.dispatch(getMeasurements(this.props.sourceName));
    }

    render() {
        return (
            <div>
                <p className="source-header">{this.props.sourceName}</p>
                <MeasurementsGraph measurements={this.props.measurements}/>;
            </div>
        )
    }
}

export default connect(
    (state, ownProps) => ({
        measurements: ownProps.sourceName in state.measurementsBySource ? state.measurementsBySource[ownProps.sourceName].data : []
    }), {
        getMeasurements
    }
)(MeasurementSourceView);