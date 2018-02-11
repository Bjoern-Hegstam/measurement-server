import React from 'react';
import {render} from 'react-dom'
import {Provider} from 'react-redux';
import {applyMiddleware, createStore} from 'redux';
import reducers from './reducers';
import axios from 'axios';
import axiosMiddleware from 'redux-axios-middleware';
import App from "./App";

require('./main.less');

const client = axios.create({
    baseURL: '/api'
});

let store = createStore(
    reducers,
    applyMiddleware(
        axiosMiddleware(client)
    )
);

render(
    <Provider store={store}>
        <App/>
    </Provider>,
    document.getElementById('root')
);