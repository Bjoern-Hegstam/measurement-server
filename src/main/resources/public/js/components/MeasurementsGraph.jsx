import React from "react";
import PropTypes from 'prop-types';
import ChartGraph from "./Chart";
import {MeasurementType} from "../types";

export default class MeasurementsGraph extends React.Component {
    static propTypes = {
        measurements: PropTypes.arrayOf(MeasurementType)
    };

    static defaultProps = {
        measurements: []
    };

    render () {
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
                data,
                lineTension: 0
            }]
        };

        const chartOptions = {
            scales: {
                xAxes: [{
                    type: 'time',
                    time: {
                        unit: 'day',
                        displayFormats: {
                            day: 'dddd D/M'
                        }
                    },
                    scaleLabel: {
                        display: true,
                        labelString: 'Date'
                    }
                }],
                yAxes: [{
                    ticks: {
                        beginAtZero: true,
                        suggestedMax: 1000
                    }
                }]
            }
        };

        return <ChartGraph type={'line'} data={chartData} options={chartOptions}/>
    }
}