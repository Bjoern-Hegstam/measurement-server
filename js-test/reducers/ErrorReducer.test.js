import reducer from '../../js/reducers/ErrorReducer';

const TYPE = 'GET_DATA';

it('ignores action without request payload', () => {
    // given
    const action = { type: TYPE };

    // when
    const newState = reducer({}, action);

    // then
    expect(newState).toEqual({});
});

it('clears error as loading', () => {
    // given
    const action = {
        type: TYPE,
        payload: {
            request: Promise.resolve(),
            payload: { request: Promise.resolve() },
        },
    };

    // when
    const newState = reducer({}, action);

    // then
    expect(newState).toEqual({
        [TYPE]: '',
    });
});

it('ignores SUCCESS', () => {
    // given
    const action = {
        type: `${TYPE}_SUCCESS`,
        payload: { request: Promise.resolve() },
    };

    // when
    const newState = reducer({}, action);

    // then
    expect(newState).toEqual({});
});

it('stores error message on FAIL', () => {
    // given
    const state = { [TYPE]: '' };

    const action = {
        type: `${TYPE}_FAIL`,
        payload: { request: Promise.resolve() },
        error: 'Error',
    };

    // when
    const newState = reducer(state, action);

    // then
    expect(newState).toEqual({
        [TYPE]: 'Error',
    });
});
