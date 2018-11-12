import reducer from '../../js/reducers/LoadingReducer';

const TYPE = 'GET_DATA';

it('ignores action without request payload', () => {
    // given
    const action = { type: TYPE };

    // when
    const newState = reducer({}, action);

    // then
    expect(newState).toEqual({});
});

it('marks request as loading', () => {
    // given
    const action = {
        type: TYPE,
        payload: { request: Promise.resolve() },
    };

    // when
    const newState = reducer({}, action);

    // then
    expect(newState).toEqual({
        [TYPE]: true,
    });
});

it('marks request as not loading on SUCCESS', () => {
    // given
    const state = { [TYPE]: true };

    const action = {
        type: `${TYPE}_SUCCESS`,
        payload: { request: Promise.resolve() },
    };

    // when
    const newState = reducer(state, action);

    // then
    expect(newState).toEqual({
        [TYPE]: false,
    });
});

it('marks request as not loading on FAIL', () => {
    // given
    const state = { [TYPE]: true };

    const action = {
        type: `${TYPE}_FAIL`,
        payload: { request: Promise.resolve() },
    };

    // when
    const newState = reducer(state, action);

    // then
    expect(newState).toEqual({
        [TYPE]: false,
    });
});
