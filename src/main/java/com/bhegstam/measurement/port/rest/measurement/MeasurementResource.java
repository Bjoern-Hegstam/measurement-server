package com.bhegstam.measurement.port.rest.measurement;

import com.bhegstam.measurement.application.MeasurementApplication;
import com.bhegstam.measurement.db.PaginationSettings;
import com.bhegstam.measurement.db.QueryResult;
import com.bhegstam.measurement.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class MeasurementResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementResource.class);

    private final MeasurementApplication measurementApplication;

    public MeasurementResource(MeasurementApplication measurementApplication) {
        this.measurementApplication = measurementApplication;
    }

    @Deprecated
    @Path("measurements")
    @POST
    public Response postMeasurement_deprecated(@Valid CreateMeasurementRequest request) {
        return postMeasurement(request);
    }

    @Path("measurement")
    @POST
    public Response postMeasurement(@Valid CreateMeasurementRequest request) {
        LOGGER.info("Received request [{}] to post measurement", request);

        measurementApplication.addMeasurement(
                request.getSource(),
                Instant.now(),
                request.getType(),
                request.getValue(),
                request.getUnit()
        );

        Response.Status status = CREATED;

        logResponse(status, null);

        return Response
                .status(status)
                .entity(null)
                .build();
    }

    @Path("instrumentation")
    @GET
    public Response getInstrumentations() {
        LOGGER.info("Received request to get all instrumentations");

        List<Instrumentation> instrumentations = measurementApplication.getInstrumentations();

        Response.Status status = OK;
        List<InstrumentationDto> body = instrumentations
                .stream()
                .map(InstrumentationDto::new)
                .collect(toList());

        logResponse(status, body);

        return Response
                .status(status)
                .entity(body)
                .build();
    }

    @Path("instrumentation/{instrumentationId}/sensor/{sensorId}/measurement")
    @GET
    public Response getMeasurementsForSensor(
            @PathParam("instrumentationId") String instrumentationIdString,
            @PathParam("sensorId") String sensorIdString,
            @QueryParam("per_page") Integer perPage,
            @QueryParam("page") Integer page
    ) {
        PaginationSettings paginationSettings = new PaginationSettings(
                perPage != null ? perPage : PaginationSettings.DEFAULT_ITEMS_PER_PAGE,
                page != null ? page : PaginationSettings.DEFAULT_PAGE
        );

        QueryResult<Measurement> queryResult = measurementApplication.getMeasurements(
                InstrumentationId.parse(instrumentationIdString),
                SensorId.parse(sensorIdString),
                paginationSettings);

        Response.Status status = OK;
        List<GetMeasurementResponse> body = queryResult
                .getData()
                .stream()
                .map(GetMeasurementResponse::new)
                .collect(toList());

        logResponse(status, body);

        Response.ResponseBuilder responseBuilder = Response
                .status(status)
                .entity(body);

        queryResult.getPaginationInformation().ifPresent(paginationInformation -> PaginationHeader.appendHeaders(responseBuilder, paginationInformation));

        return responseBuilder
                .build();
    }

    private void logResponse(Response.Status status, Object body) {
        LOGGER.info("Responding to request with status [{}] and body [{}]", status, body);
    }
}
