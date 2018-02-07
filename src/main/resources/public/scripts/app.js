import $ from "jquery";
import chart from "chart.js";
import db from "./db/measurement";

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
            const measurements = args.measurements.concat(newMeasurements);
            const existsMore = jqXHR.getResponseHeader('X-Next-Page') !== null;
            const lastRetrievedCreatedAfterCutoff = new Date(measurements[measurements.length - 1].createdAtMillis) > args.createdAfter;

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

function createGraph(measurements) {
    const data = [];
    measurements.forEach(function (m) {
        data.push({
            x: new Date(m.createdAtMillis),
            y: m.value
        });
    });

    const $canvas = $('<canvas>');
    const $context = $canvas[0].getContext('2d');
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
                        beginAtZero: true
                    }
                }]
            }
        }
    });

    return $canvas;
}

$(document).ready(() => {
    db.getSources()
        .done(function (sources) {
            sources.sort(function (a, b) {
                return a.name.localeCompare(b.name);
            });

            sources.forEach(function (source) {
                const $container = $('<div>');
                $('#dashboard-container').append($container);

                const createdAfter = new Date();
                createdAfter.setDate(createdAfter.getDate() - 7); // One week ago

                getMeasurements(source, {createdAfter: createdAfter})
                    .then(function (measurements) {
                        $container.append(
                            $('<p>', {class: 'source-header'}).text(source.name),
                            createGraph(measurements)
                        );
                    });
            })
        });
});