import React from "react";
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {getMeasurements} from "../actions/MeasurementsActions";
import ChartGraph from './Chart';

class MeasurementSourceView extends React.Component {
    static propTypes = {
        sourceName: PropTypes.string.isRequired,
        dispatch: PropTypes.func.isRequired,

        measurements: PropTypes.arrayOf(PropTypes.object)
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
                {this.renderMeasurementsGraph()}
            </div>
        )
    }

    renderMeasurementsGraph() {
        const data = this.props.measurements
            .map(m => ({
                x: new Date(m.createdAtMillis),
                y: m.value
            }));

        const chartData = {
            datasets: [{
                label: 'Measurements',
                backgroundColor: 'rgba(255, 0, 0, 0.1)',
                borderColor: 'rgba(255, 0, 0, 0.5)',
                borderWidth: 1,
                data: data,
                lineTension: 0
            }]
        };

        const chartOptions = {
            scales: {
                xAxes: [{
                    type: 'time',
                    scaleLabel: {
                        display: true,
                        labelString: 'Date'
                    }
                }],
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        };

        return <ChartGraph type={'line'} data={chartData} options={chartOptions}/>
    }
}

export default connect((state, ownProps) => {
    return {
        measurements: ownProps.sourceName in state.measurementsBySource ? state.measurementsBySource[ownProps.sourceName].data : []
    }
})(MeasurementSourceView);