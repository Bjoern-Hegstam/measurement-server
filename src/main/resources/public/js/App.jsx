import React from "react";
import {connect} from 'react-redux';
import {getMeasurementSources, getMeasurements} from "./actions/MeasurementsActions";
import PropTypes from 'prop-types';

import {Line as LineChart} from "react-chartjs";

class App extends React.Component {
    static propTypes = {
        measurementSources: PropTypes.arrayOf(
            PropTypes.shape({
                name: PropTypes.string
            })
        ),
        measurementsPerSource: PropTypes.shape({
            sourceName: PropTypes.shape({
                source: PropTypes.shape({
                    name: PropTypes.string
                }),
                createdAtMillis: PropTypes.number,
                type: PropTypes.string,
                value: PropTypes.number,
                unit: PropTypes.string
            })
        }),
        dispatch: PropTypes.func.isRequired
    };

    static defaultProps = {
        measurementSources: [],
        measurementsPerSource: {}
    };

    componentDidMount() {
        this.props.dispatch(getMeasurementSources())
            .then((response) => {
                // TODO: Limit fetch based on most recently retrieved measurement
                const sources = response.payload.data;
                sources.map((source) => this.props.dispatch(getMeasurements(source.name)));
            })
            .catch(response => {
                console.log(`Error while loading data: ${response}`);
            });
    }

    render() {
        return (
            <div>
                {this.renderSources()}
            </div>
        );
    }

    renderSources() {
        return this.props.measurementSources
            .filter(source => source.name in this.props.measurementsPerSource)
            .map(source => this.renderMeasurementSource(source.name));
    }

    renderMeasurementSource(sourceName) {
        return (
            <div>
                <p className="source-header">{sourceName}</p>
                {this.renderMeasurementsGraph(sourceName)}
            </div>
        )
    }

    renderMeasurementsGraph(sourceName) {
        const data = this.props.measurementsPerSource[sourceName]
            .map(m => ({
                x: new Date(m.createdAtMillis),
                y: m.value
            }));

        const chartData = [{
            label: 'Measurements',
            backgroundColor: 'rgba(255, 0, 0, 0.1)',
            borderColor: 'rgba(255, 0, 0, 0.5)',
            borderWidth: 1,
            data: data,
            lineTension: 0
        }];

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


        return <LineChart data={chartData} options={chartOptions}/>
    }
}

export default connect(store => {
    // TODO: Check measurements.isFetching and measurements.error
    return {
        measurementSources: store.measurements.sources,
        measurementsPerSource: store.measurements.measurementsPerSource
    }
})(App);