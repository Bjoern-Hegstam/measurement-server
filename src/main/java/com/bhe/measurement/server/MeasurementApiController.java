package com.bhe.measurement.server;

import com.bhe.web.util.JsonResponseTransformer;
import com.github.bhe.webutil.webapp.Controller;
import com.google.inject.Inject;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Service;

import java.time.LocalDateTime;
import java.util.List;

public class MeasurementApiController implements Controller {

    private final MeasurementRepository measurementRepository;

    @Inject
    public MeasurementApiController(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
        ;
    }

    @Override
    public void configureRoutes(Service http) {
        http.get("/measurement", "application/json", this::getMeasurements, new JsonResponseTransformer());
        http.post("/measurement", "application/json", this::postMeasurement, new JsonResponseTransformer());
    }


    private List<MeasurementBean> getMeasurements(Request request, Response response) {
        return measurementRepository.getAll();
    }

    private MeasurementBean postMeasurement(Request request, Response response) {
        MeasurementBean measurement = MeasurementBean.fromJson(request.body());
        MeasurementBean newMeasurement = measurementRepository.create(measurement);

        System.out.println("Inserting measurement");
        System.out.println(LocalDateTime.now() + ": " + newMeasurement.toString());
        response.type("application/json");
        response.status(HttpStatus.OK_200);
        return newMeasurement;
    }
}
