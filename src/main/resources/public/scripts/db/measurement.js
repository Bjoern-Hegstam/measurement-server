import $ from "jquery";

export default {
    getSources: () => $.get({
        url: '/api/sources',
        dataType: 'json'
    }),

    getMeasurements: (source, args) => {
        let url = `/api/sources/${source}/measurements?`;
        if (arguments.length === 2) {
            url += $.param(args);
        }

        return $.get({
            url: url,
            dataType: 'json'
        });
    }
}