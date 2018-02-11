import React from "react";
import {connect} from 'react-redux';
import {getMeasurementSources} from "./actions/MeasurementsActions";
import PropTypes from 'prop-types';
import MeasurementSourceView from './components/MeasurementSourceView';

class App extends React.Component {
    static propTypes = {
        measurementSources: PropTypes.arrayOf(
            PropTypes.shape({
                name: PropTypes.string
            })
        ),
        dispatch: PropTypes.func.isRequired
    };

    static defaultProps = {
        measurementSources: []
    };

    componentDidMount() {
        this.props.dispatch(getMeasurementSources())
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
        return this.props
            .measurementSources
            .map(source => <MeasurementSourceView key={source.name} sourceName={source.name} dispatch={this.props.dispatch}/>);
    }
}

export default connect(store => {
    return {
        measurementSources: store.sources
    }
})(App);