define(['jquery', 'app/db/measurement'], function ($, db) {

    db.getSources()
        .done(function (sources) {
            sources.forEach(function (source) {
                var createdAfter = new Date();
                createdAfter.setDate(createdAfter.getDate() - 7); // One week ago

                getMeasurements(source, {createdAfter: createdAfter})
                    .then(function (measurements) {
                        var $dataTable = createTable(measurements);
                        var $container = $('<div>').append(
                            $('<p>').text(source.name),
                            $dataTable
                        );
                        $('#dashboard-container').append($container);
                    });
            })
        });

    function getMeasurements(source, args) {
        if (!args.hasOwnProperty('measurements')) {
            args.measurements = []
        }

        if (!args.hasOwnProperty('page')) {
            args.page = 1;
        }

        return db
            .getMeasurements(source.name, {page: args.page})
            .then(function (newMeasurements, textStatus, jqXHR) {
                var measurements = args.measurements.concat(newMeasurements);
                var existsMore = jqXHR.getResponseHeader('X-Next-Page') !== null;
                var lastRetrievedCreatedAfterCutoff = toLocalTimestamp(measurements[measurements.length - 1].createdAtMillis) > args.createdAfter;

                if (existsMore && lastRetrievedCreatedAfterCutoff) {
                    return getMeasurements(source, {
                        measurements: measurements,
                        page: args.page + 1,
                        createdAfter: args.createdAfter
                    });
                } else {
                    return measurements.filter(function (m) {
                        return toLocalTimestamp(m.createdAtMillis) > args.createdAfter;
                    });
                }
            })
    }

    function createTable(measurements) {
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

        return $dataTable;
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