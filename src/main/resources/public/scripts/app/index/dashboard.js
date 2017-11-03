define(['jquery', 'app/db/measurement'], function ($, db) {
    function Attribute(niceName, dataName, dataType) {
        return {
            niceName: niceName,
            dataName: dataName,
            dataType: dataType
        }
    }

    var MeasurementAttributes = [
        Attribute('Source', 'source', 'string'),
        Attribute('Type', 'type', 'string'),
        Attribute('Value', 'value', 'numeric'),
        Attribute('Created at', 'createdAtMillis', 'timestamp')
    ];

    db.getSources()
        .done(function (sources) {
            sources.forEach(function (source) {
                db.getMeasurements(source.name)
                    .done(function (measurements) {
                        console.log(measurements);
                    });
            })
        });

/*    db.getMeasurements()
        .done(function (measurements) {
            var dataTable = document.createElement('table');
            dataTable.classList.add('data-table');

            // Table header
            var header = document.createElement('thead');
            dataTable.appendChild(header);

            MeasurementAttributes.forEach(function (a) {
                var td = document.createElement('td');
                td.innerText = a.niceName;
                header.appendChild(td);
            });

            // Measurement rows
            measurements.forEach(function (measurement) {
                var row = document.createElement('tr');
                MeasurementAttributes.forEach(function (a) {
                    var td = document.createElement('td');

                    if (a.dataType === 'timestamp') {
                        var currentDate = new Date();
                        var timestamp = new Date(measurement[a.dataName]);
                        var localTimestamp = new Date(timestamp.getTime() - currentDate.getTimezoneOffset() * 60000); // Convert min to ms
                        td.innerText = localTimestamp.toISOString();
                    } else {
                        td.innerText = measurement[a.dataName];
                    }
                    td.classList.add(a.dataType);
                    row.appendChild(td);
                });
                dataTable.appendChild(row);
            });

            // Show user new table
            var container = document.getElementById('dashboard-container');

            if (container.childElementCount > 0) {
                container.removeChild(container.firstChild)
            }

            container.appendChild(dataTable)

        });*/
});