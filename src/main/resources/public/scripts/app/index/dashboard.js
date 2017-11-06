define(['jquery', 'app/db/measurement'], function ($, db) {

    db.getSources()
        .done(function (sources) {
            clearTables();
            sources.forEach(function (source) {
                var createdAfter = new Date();
                createdAfter.setDate(createdAfter.getDate() - 7); // One week ago
                loadAndVisualizeMeasurements(source, createdAfter);
            })
        });

    function loadAndVisualizeMeasurements(source, createdAfter) {
        _loadAndVisualizeMeasurements(source, {
            measurements: [],
            page: 1,
            createdAfter: createdAfter
        })
    }

    function _loadAndVisualizeMeasurements(source, args) {
        db.getMeasurements(source.name, {page: args.page})
            .done(function (newMeasurements, textStatus, jqXHR) {
                var measurements = args.measurements.concat(newMeasurements);
                var getMoreMeasurements = jqXHR.getResponseHeader('X-Next-Page') !== null;

                if (getMoreMeasurements && toLocalTimestamp(measurements[measurements.length - 1].createdAtMillis) > args.createdAfter) {
                    _loadAndVisualizeMeasurements(source, {
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

                    updateTable(source, measurementsToPlot);
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