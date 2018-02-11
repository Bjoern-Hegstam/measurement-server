import React from "react";
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {getMeasurements} from "../actions/MeasurementsActions";
import MeasurementsGraph from './MeasurementsGraph';

class MeasurementSourceView extends React.Component {
    static propTypes = {
        sourceName: PropTypes.string.isRequired,
        dispatch: PropTypes.func.isRequired,

        measurements: PropTypes.arrayOf(PropTypes.shape({
                source: PropTypes.shape({
                    name: PropTypes.string
                }),
                createdAtMillis: PropTypes.number,
                type: PropTypes.string,
                value: PropTypes.number,
                unit: PropTypes.string
            })
        )
    };

    static defaultProps = {
        measurements: []
    };

    componentDidMount() {
        this.props.dispatch(getMeasurements(this.props.sourceName));
    }

    render() {
        const data = this.props.measurements
            .map(m => ({
                x: new Date(m.createdAtMillis),
                y: m.value
            }));

        return (
            <div>
                <p className="source-header">{this.props.sourceName}</p>
                <MeasurementsGraph data={data}/>;
            </div>
        )
    }
}

export default connect((state, ownProps) => {
    return {
        measurements: ownProps.sourceName in state.measurementsBySource ? state.measurementsBySource[ownProps.sourceName].data : []
    }
})(MeasurementSourceView);