define(['jquery', 'app/db/measurement'], function ($, db) {

    db.getSources()
        .done(function (sources) {
            clearTables();
            sources.forEach(function (source) {
                loadAndVisualizeMeasurements(source);
            })
        });

    function loadAndVisualizeMeasurements(source, loadedMeasurements, page) {
        if (arguments.length < 3) {
            page = 1;
        }

        if (arguments.length < 2) {
            loadedMeasurements = [];
        }

        db.getMeasurements(source.name, {page: page})
            .done(function (newMeasurements, textStatus, jqXHR) {
                var measurements = loadedMeasurements.concat(newMeasurements);
                var getMoreMeasurements = jqXHR.getResponseHeader('X-Next-Page') !== null;

                if (getMoreMeasurements) {
                    loadAndVisualizeMeasurements(source, measurements, page + 1);
                } else {
                    updateTable(source, measurements)
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
        var currentDate = new Date();
        var timestamp = new Date(timestamp_str);
        var localTimestamp = new Date(timestamp.getTime() - currentDate.getTimezoneOffset() * 60000); // Convert min to ms
        return localTimestamp.toISOString()
    }
});