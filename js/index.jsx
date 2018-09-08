import React from 'react';
import {render} from 'react-dom'
import {Provider} from 'react-redux';
import {applyMiddleware, createStore} from 'redux';
import reducers from './reducers/index';
import axios from 'axios';
import axiosMiddleware from 'redux-axios-middleware';
import App from "./App";
import {loadState, saveState} from "./store";

require('./main.scss');

const client = axios.create({
    baseURL: '/api'
});

const persistedState = loadState();

let store = createStore(
    reducers,
    persistedState,
    applyMiddleware(
        axiosMiddleware(client)
    )
);

store.subscribe(() => saveState(store));

render(
    <Provider store={store}>
        <App/>
    </Provider>,
    document.getElementById('root')
);