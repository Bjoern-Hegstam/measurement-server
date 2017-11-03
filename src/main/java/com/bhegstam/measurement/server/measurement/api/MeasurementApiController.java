package com.bhegstam.measurement.server.measurement.api;

import com.bhegstam.measurement.server.db.PaginationSettings;
import com.bhegstam.measurement.server.db.QueryResult;
import com.bhegstam.measurement.server.measurement.db.Measurement;
import com.bhegstam.measurement.server.measurement.db.MeasurementRepository;
import com.bhegstam.measurement.server.util.AcceptType;
import com.bhegstam.measurement.server.util.JsonResponseTransformer;
import com.bhegstam.measurement.server.util.Path;
import com.bhegstam.measurement.server.web.PaginationHeader;
import com.bhegstam.webutil.webapp.Controller;
import com.bhegstam.webutil.webapp.Request;
import com.bhegstam.webutil.webapp.Result;
import com.bhegstam.webutil.webapp.ResultBuilder;
import com.google.inject.Inject;
import spark.Service;

import java.util.function.Function;

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
        JsonResponseTransformer jsonResponseTransformer = new JsonResponseTransformer();

        http.get(Path.Api.SOURCES, AcceptType.APPLICATION_JSON, asSparkRoute(this::getSources), jsonResponseTransformer);
        http.path(
                Path.Api.SOURCES,
                () -> {
                    http.get("", asSparkRoute(this::getSources), jsonResponseTransformer);
                    http.get("/:source" + Path.Api.MEASUREMENTS, asSparkRoute(this::getMeasurementsForSource), jsonResponseTransformer);

                }
        );

        http.post(Path.Api.MEASUREMENTS, AcceptType.APPLICATION_JSON, asSparkRoute(this::postMeasurement), jsonResponseTransformer);
    }

    Result getSources(Request request) {
        QueryResult<String> queryResult = measurementRepository.findSources();
        return resultFromQueryResult(queryResult, MeasurementSource::new);
    }

    Result getMeasurementsForSource(Request request) {
        QueryResult<Measurement> queryResult = measurementRepository.find(request.params("source"), PaginationSettings.fromQuery(request));
        return resultFromQueryResult(queryResult, MeasurementBean::fromDbBean);
    }

    private <T> Result resultFromQueryResult(QueryResult<T> queryResult, Function<T, ?> dataFun) {
        ResultBuilder resultBuilder = result();

        queryResult.getPaginationInformation().ifPresent(pageInfo -> PaginationHeader.appendHeaders(resultBuilder, pageInfo));

        return resultBuilder
                .type(AcceptType.APPLICATION_JSON)
                .returnPayload(
                        queryResult
                                .getData().stream()
                                .map(dataFun)
                                .collect(toList())
                );
    }

    Result postMeasurement(Request request) {
        MeasurementBean measurement = MeasurementBean.fromJson(request.body());
        measurement.setCreatedAtMillis(System.currentTimeMillis());

        Measurement newMeasurement = measurementRepository.create(measurement.toDbBean());

        return result()
                .type(AcceptType.APPLICATION_JSON)
                .returnPayload(MeasurementBean.fromDbBean(newMeasurement));
    }
}
