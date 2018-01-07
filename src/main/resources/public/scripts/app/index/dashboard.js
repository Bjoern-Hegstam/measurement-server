define(['jquery',  'chart', 'app/db/measurement'], function ($, chart, db) {

    db.getSources()
        .done(function (sources) {
            sources.sort(function (a, b) {
                return a.name.localeCompare(b.name);
            });

            sources.forEach(function (source) {
                var $container = $('<div>');
                $('#dashboard-container').append($container);

                var createdAfter = new Date();
                createdAfter.setDate(createdAfter.getDate() - 7); // One week ago

                getMeasurements(source, {createdAfter: createdAfter})
                    .then(function (measurements) {
                        var $dataTable = createTable(measurements);
                        var $graph = createGraph(measurements);

                        $container.append(
                            $('<p>', {class: 'source-header'}).text(source.name),
                            $graph,
                            $('<details>').append($dataTable)
                        );
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
                var lastRetrievedCreatedAfterCutoff = new Date(measurements[measurements.length - 1].createdAtMillis) > args.createdAfter;

                if (existsMore && lastRetrievedCreatedAfterCutoff) {
                    return getMeasurements(source, {
                        measurements: measurements,
                        page: args.page + 1,
                        createdAfter: args.createdAfter
                    });
                } else {
                    return measurements.filter(function (m) {
                        return new Date(m.createdAtMillis) > args.createdAfter;
                    });
                }
            })
    }

    function createTable(measurements) {
        var $dataTable = $('<table>', {class: 'data-table'});

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
                    $('<td>').text(new Date(measurement.createdAtMillis).toLocaleString())
                )
            );
        });

        return $dataTable;
    }

    function createGraph(measurements) {
        var data = [];
        measurements.forEach(function (m) {
            data.push({
                x: new Date(m.createdAtMillis),
                y: m.value
            });
        });

        var $canvas = $('<canvas>');
        var $context = $canvas[0].getContext('2d');
        new chart.Chart($context, {
            type: 'line',
            data: {
                datasets: [{
                    label: 'Measurements',
                    backgroundColor: 'rgba(255, 0, 0, 0.1)',
                    borderColor: 'rgba(255, 0, 0, 0.5)',
                    borderWidth: 1,
                    data: data,
                    lineTension: 0
                }]
            },
            options: {
                scales: {
                    xAxes: [{
                        type: 'time',
                        scaleLabel: {
                            display: true,
                            labelString: 'Date'
                        }
                    }],
                    yAxes: [{
                        ticks: {
                            beginAtZero:true
                        }
                    }]
                }
            }
        });

        return $canvas;
    }
});
