define(['jquery'], function ($) {
    return {
        getSources: function () {
            return $.get({
                url: '/api/sources',
                dataType: 'json'
            })
        },

        getMeasurements: function (source) {
            return $.get({
                url: '/api/sources/' + source + '/measurements',
                dataType: 'json'
            })
        }
    }
});