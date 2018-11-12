import React from "react";
import { connect } from 'react-redux';
import { getInstrumentations } from "../actions/MeasurementsActions";
import PropTypes from 'prop-types';
import { createErrorSelector, createLoadingSelector, getInstrumentationsSelector } from '../selectors';
import { InstrumentationType } from '../types';
import Instrumentation from '../components/Instrumentation';
import * as types from '../actions/types';

class InstrumentationsPage extends React.Component {
    static propTypes = {
        instrumentations: PropTypes.arrayOf(InstrumentationType),

        getInstrumentations: PropTypes.func.isRequired,
        fetchingInstrumentations: PropTypes.bool.isRequired,
        errorGetInstrumentations: PropTypes.object,
    };

    static defaultProps = {
        instrumentations: []
    };

    componentDidMount() {
        this.props.getInstrumentations();
    }

    componentDidUpdate(prevProps) {
        if (prevProps.fetchingInstrumentations && !this.props.fetchingInstrumentations) {
            if (this.props.errorGetInstrumentations) {
                console.error(`Error while fetching instrumentations: ${this.props.errorGetInstrumentations}`);
            }
        }
    }

    render() {
        const { instrumentations } = this.props;

        return (
            <div>
                {instrumentations.map(instrumentation => <Instrumentation{...instrumentation} />)}
            </div>
        );
    }
}

const mapStateToProps = state => ({
    instrumentations: getInstrumentationsSelector(state),

    fetchingInstrumentations: createLoadingSelector(types.GET_INSTRUMENTATIONS)(state),
    errorGetInstrumentations: createErrorSelector(types.GET_INSTRUMENTATIONS)(state),
});

const mapDispatchToProps = dispatch => ({
    getInstrumentations: () => dispatch(getInstrumentations()),
});

export default connect(mapStateToProps, mapDispatchToProps)(InstrumentationsPage);