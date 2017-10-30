package com.bhegstam.measurement.server.measurement.api;

import com.bhegstam.measurement.server.db.PaginationSettings;
import com.bhegstam.measurement.server.measurement.db.DbMeasurementBean;
import com.bhegstam.measurement.server.measurement.db.MeasurementRepository;
import com.bhegstam.measurement.server.util.AcceptType;
import com.bhegstam.measurement.server.util.JsonResponseTransformer;
import com.bhegstam.measurement.server.util.Path;
import com.bhegstam.webutil.webapp.Controller;
import com.bhegstam.webutil.webapp.Request;
import com.bhegstam.webutil.webapp.Result;
import com.google.inject.Inject;
import spark.Service;

import java.util.List;

import static com.bhegstam.webutil.webapp.ResultBuilder.result;
import static com.bhegstam.webutil.webapp.SparkWrappers.asSparkRoute;
import static java.util.stream.Collectors.toList;

public class MeasurementApiController implements Controller {

    private final MeasurementRepository measurementRepository;

    @Inject
    public MeasurementApiController(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Override
    public void configureRoutes(Service http) {
        http.get(Path.Api.MEASUREMENT, AcceptType.APPLICATION_JSON, asSparkRoute(this::getMeasurements), new JsonResponseTransformer());
        http.post(Path.Api.MEASUREMENT, AcceptType.APPLICATION_JSON, asSparkRoute(this::postMeasurement), new JsonResponseTransformer());
    }

    private Result getMeasurements(Request request) {
        List<MeasurementBean> measurements = measurementRepository
                .find(PaginationSettings.fromQuery(request)).stream()
                .map(MeasurementBean::fromDbBean)
                .collect(toList());

        return result()
                .type(AcceptType.APPLICATION_JSON)
                .returnPayload(measurements);
    }

    private Result postMeasurement(Request request) {
        MeasurementBean measurement = MeasurementBean.fromJson(request.body());
        measurement.setCreatedAtMillis(System.currentTimeMillis());

        DbMeasurementBean newMeasurement = measurementRepository.create(measurement.toDbBean());

        return result()
                .type(AcceptType.APPLICATION_JSON)
                .returnPayload(MeasurementBean.fromDbBean(newMeasurement));
    }
}
