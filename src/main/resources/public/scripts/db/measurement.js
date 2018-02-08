import $ from "jquery";

export function getSources() {
    return $.get({
        url: '/api/sources',
        dataType: 'json'
    })
}

export function getMeasurements(source, args) {
    let url = `/api/sources/${source}/measurements`;
    if (arguments.length === 2) {
        url += '?' + $.param(args);
    }

    console.log(`Calling ${url}`);

    return $.get({
        url: url,
        dataType: 'json'
    });
}