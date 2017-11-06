define(['jquery', 'app/db/measurement'], function ($, db) {

    db.getSources()
        .done(function (sources) {
            clearTables();
            sources.forEach(function (source) {
                var createdAfter = new Date();
                createdAfter.setDate(createdAfter.getDate() - 7); // One week ago

                getMeasurements(source, createdAfter)
                    .then(function (measurements) {
                        updateTable(source, measurements)
                    });
            })
        });

    function getMeasurements(source, createdAfter) {
        return new Promise(
            function (resolve) {
                _getMeasurements(source, resolve, {
                    measurements: [],
                    page: 1,
                    createdAfter: createdAfter
                })
            }
        );
    }

    function _getMeasurements(source, callback, args) {
        db.getMeasurements(source.name, {page: args.page})
            .done(function (newMeasurements, textStatus, jqXHR) {
                var measurements = args.measurements.concat(newMeasurements);
                var existsMore = jqXHR.getResponseHeader('X-Next-Page') !== null;
                var lastRetrievedCreatedAfterCutoff = toLocalTimestamp(measurements[measurements.length - 1].createdAtMillis) > args.createdAfter;

                if (existsMore && lastRetrievedCreatedAfterCutoff) {
                    _getMeasurements(source, callback, {
                        measurements: measurements,
                        page: args.page + 1,
                        createdAfter: args.createdAfter
                    });
                } else {
                    var measurementsToPlot = [];
                    measurements.forEach(function (m) {
                        if (toLocalTimestamp(m.createdAtMillis) > args.createdAfter) {
                            measurementsToPlot.push(m);
                        }
                    });

                    callback(measurementsToPlot);
                }
            })
    }

    function clearTables() {
        $('#dashboard-container').empty();
    }

    function updateTable(source, measurements) {
        var $dataTable = $('<table>', {'class': 'data-table'});

        // Table header
        $dataTable.append(
            $('<thead>').append(
                $('<td>').text('Source'),
                $('<td>').text('Type'),
                $('<td>').text('Value'),
                $('<td>').text('Created at')
            )
        );

        // Measurement rows
        measurements.forEach(function (measurement) {
            $dataTable.append(
                $('<tr>').append(
                    $('<td>').text(measurement.source.name),
                    $('<td>').text(measurement.type),
                    $('<td>').text(measurement.value),
                    $('<td>').text(formatTimestamp(measurement.createdAtMillis))
                )
            );
        });

        // Show user new table
        var $container = $('<div>').append(
            $('<p>').text(source.name),
            $dataTable
        );

        $('#dashboard-container').append($container);
    }

    function formatTimestamp(timestamp_str) {
        var localTimestamp = toLocalTimestamp(timestamp_str);
        return localTimestamp.toISOString()
    }

    function toLocalTimestamp(timestamp_str) {
        var currentDate = new Date();
        var timestamp = new Date(timestamp_str);
        return new Date(timestamp.getTime() - currentDate.getTimezoneOffset() * 60000);  // Convert min to ms
    }
});