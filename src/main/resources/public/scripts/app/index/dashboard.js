define(['jquery', 'app/db/measurement'], function ($, db) {

    db.getSources()
        .done(function (sources) {
            clearTables();
            sources.forEach(function (source) {
                db.getMeasurements(source.name)
                    .done(function (measurements) {
                        updateTable(source, measurements);
                    });
            })
        });

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