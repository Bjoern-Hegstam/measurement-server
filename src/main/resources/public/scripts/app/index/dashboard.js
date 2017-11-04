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

        db.getMeasurements(source.name) // TODO: Need to add page query args
            .done(function (measurements) { // TODO: Callback needs to get pagination information (maybe provided automatically if we just add another parameter? Check jquery docs)
                var newMeasurements = loadedMeasurements.concat(measurements);
                var getMoreMeasurements = false; // TODO: Check if more are required and there is another page

                if (getMoreMeasurements) {
                    loadAndVisualizeMeasurements(source, newMeasurements, page + 1);
                } else {
                    // ELSE draw graph/fill table
                    updateTable(source, newMeasurements)
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