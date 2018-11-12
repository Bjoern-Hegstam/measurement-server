export default function (state = {}, action) {
    const { type } = action;

    let requestName;
    let errorMessage;

    const matches = /(.*)_FAIL/.exec(type);
    if (matches) {
        [, requestName] = matches;
        errorMessage = action.error;
    } else if (action.payload && action.payload.request && !type.endsWith('SUCCESS')) {
        requestName = action.type;
        errorMessage = '';
    } else {
        return state;
    }

    return {
        ...state,
        [requestName]: errorMessage,
    };
}
