export default function (state = {}, action) {
    const { type } = action;

    let requestName;
    let isLoading;

    // TODO: Add support for running multiple actions of same type in parallel. Maybe by including metadata in action, e.g. loading: indexed

    const matches = /(.*)_(?:SUCCESS|FAIL)/.exec(type);
    if (matches) {
        [, requestName] = matches;
        isLoading = false;
    } else if (action.payload && action.payload.request) {
        requestName = type;
        isLoading = true;
    } else {
        return state;
    }

    return {
        ...state,
        [requestName]: isLoading,
    };
}
