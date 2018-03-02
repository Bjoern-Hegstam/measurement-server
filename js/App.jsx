import React from "react";
import {connect} from 'react-redux';
import {getMeasurementSources} from "./actions/MeasurementsActions";
import PropTypes from 'prop-types';
import MeasurementSourceView from './components/MeasurementSourceView';

class App extends React.Component {
    static propTypes = {
        getMeasurementSources: PropTypes.func.isRequired,
        measurementSources: PropTypes.arrayOf(
            PropTypes.shape({
                name: PropTypes.string
            })
        )
    };

    static defaultProps = {
        measurementSources: []
    };

    componentDidMount() {
        this.props
            .getMeasurementSources()
            .catch(response => {
                console.log(`Error while loading data: ${response}`);
            });
    }

    render() {
        return (
            <div>
                {this.renderMeasurementSources()}
            </div>
        );
    }

    renderMeasurementSources() {
        return this
            .props
            .measurementSources
            .map(source => <MeasurementSourceView key={source.name} sourceName={source.name}/>);
    }
}

export default connect(
    store => ({
        measurementSources: store.sources
    }), {
        getMeasurementSources
    }
)(App);