import React from "react";
import InstrumentationsPage from './page/InstrumentationsPage';
import { Route, Switch, withRouter } from 'react-router-dom';

class App extends React.Component {
    render() {
        return (
            <Switch>
                <Route path="/" component={InstrumentationsPage} />
            </Switch>
        );
    }
}

export default withRouter(App);