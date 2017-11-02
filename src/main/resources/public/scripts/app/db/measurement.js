define(['jquery'], function ($) {
    return {
        getMeasurements: function () {
            return $.get({
                url: '/api/measurement',
                dataType: 'json'
            })
        }
    }
});