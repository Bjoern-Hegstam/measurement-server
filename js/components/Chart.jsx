import React from "react";
import Chart from 'chart.js';

export default class ChartGraph extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            chart: null
        }
    }

    componentDidMount() {
        const chartCanvas = this.refs.chart;
        const myChart = new Chart(chartCanvas, {
            type: this.props.type,
            data: this.props.data,
            options: this.props.options
        });

        this.setState({chart: myChart});
    }

    componentDidUpdate() {
        let chart = this.state.chart;
        let data = this.props.data;

        data.datasets.forEach((dataset, i) => chart.data.datasets[i].data = dataset.data);

        chart.update();
    }

    render() {
        return (
            <canvas ref={'chart'}/>
        )
    }
}