export function loadState() {
    const persistedState = localStorage.getItem('STATE');
    if (persistedState) {
        return JSON.parse(persistedState);
    }
    return {};
}

export function saveState(store) {
    localStorage.setItem('STATE', JSON.stringify(store.getState()));
}