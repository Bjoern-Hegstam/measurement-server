define(['jquery'], function ($) {
    return {
        getSources: function () {
            return $.get({
                url: '/api/sources',
                dataType: 'json'
            });
        },

        getMeasurements: function (source, args) {
            var url = '/api/sources/' + source + '/measurements?';
            if (arguments.length === 2) {
                url += $.param(args);
            }

            return $.get({
                url: url,
                dataType: 'json'
            });
        }
    }
});