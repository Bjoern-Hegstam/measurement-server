package com.bhegstam.measurement.server.measurement.api;

import com.bhegstam.measurement.server.measurement.db.DbMeasurementBean;
import com.bhegstam.measurement.server.measurement.db.MeasurementRepository;
import com.bhegstam.measurement.server.util.AcceptType;
import com.bhegstam.measurement.server.util.Path;
import com.bhegstam.measurement.server.util.JsonResponseTransformer;
import com.bhegstam.webutil.webapp.Controller;
import com.google.inject.Inject;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class MeasurementApiController implements Controller {

    private final MeasurementRepository measurementRepository;

    @Inject
    public MeasurementApiController(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Override
    public void configureRoutes(Service http) {
        http.get(Path.Api.MEASUREMENT, AcceptType.APPLICATION_JSON, this::getMeasurements, new JsonResponseTransformer());
        http.post(Path.Api.MEASUREMENT, AcceptType.APPLICATION_JSON, this::postMeasurement, new JsonResponseTransformer());
    }


    private List<MeasurementBean> getMeasurements(Request request, Response response) {
        List<MeasurementBean> measurements = measurementRepository
                .getAll().stream()
                .map(MeasurementBean::fromDbBean)
                .collect(toList());

        response.type(AcceptType.APPLICATION_JSON);
        response.status(HttpStatus.OK_200);

        return measurements;
    }

    private MeasurementBean postMeasurement(Request request, Response response) {
        MeasurementBean measurement = MeasurementBean.fromJson(request.body());
        measurement.setCreatedAtMillis(System.currentTimeMillis());

        DbMeasurementBean newMeasurement = measurementRepository.create(measurement.toDbBean());

        System.out.println("Inserting measurement");
        System.out.println(LocalDateTime.now() + ": " + newMeasurement.toString());
        response.type(AcceptType.APPLICATION_JSON);
        response.status(HttpStatus.OK_200);
        return MeasurementBean.fromDbBean(newMeasurement);
    }
}
