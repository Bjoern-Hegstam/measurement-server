import React from "react";
import PropTypes from 'prop-types';
import ChartGraph from "./Chart";

export default class MeasurementsGraph extends React.Component {
    static propTypes = {
        data: PropTypes.arrayOf(PropTypes.shape({
                x: PropTypes.number,
                y: PropTypes.number,
            })
        )
    };

    static defaultProps = {
        data: []
    };

    render () {
        const chartData = {
            datasets: [{
                label: 'Measurements',
                backgroundColor: 'rgba(255, 0, 0, 0.1)',
                borderColor: 'rgba(255, 0, 0, 0.5)',
                borderWidth: 1,
                data: this.props.data,
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