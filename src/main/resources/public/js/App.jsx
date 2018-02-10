import React from "react";
import {connect} from 'react-redux';
import {getMeasurementSources} from "./actions/MeasurementsActions";
import PropTypes from 'prop-types';

class App extends React.Component {
    static propTypes = {
        sensors: PropTypes.arrayOf(
            PropTypes.shape({
                name: PropTypes.string
            })
        ),
        dispatch: PropTypes.func.isRequired
    };

    static defaultProps = {
        sensors: []
    };

    componentDidMount() {
        this.props.dispatch(getMeasurementSources())
            .catch(response => {
                console.log(`Failed to get measurement sources: ${response.error}`);
            });
    }


    render() {
        return (
            <div>
                <ul>
                    {this.props.sensors.map((sensor) => <li>{sensor.name}</li>)}
                </ul>
            </div>
        );
    }
}

export default connect(store => {
    return {
        sensors: store.measurements.sensors
    }
})(App);