package com.bhegstam.measurement.port.rest.measurement;

import com.bhegstam.measurement.domain.InstrumentationId;
import com.bhegstam.measurement.domain.SensorId;
import com.fasterxml.jackson.databind.JsonNode;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

class MeasurementApi {
    private final WebTarget webTarget;

    MeasurementApi(String serviceUrl) {
        webTarget = ClientBuilder
                .newClient()
                .target(serviceUrl);
    }

    Response getInstrumentations() {
        return webTarget
                .path("instrumentation")
                .request()
                .get();
    }

    Response postMeasurement(JsonNode json) {
        return webTarget
                .path("measurement")
                .request()
                .post(Entity.json(json));
    }

    Response getMeasurements(InstrumentationId instrumentationId, SensorId sensorId, int perPage, int page) {
        return webTarget
                .path("instrumentation")
                .path(instrumentationId.getId())
                .path("sensor")
                .path(sensorId.getId())
                .path("measurement")
                .queryParam("per_page", perPage)
                .queryParam("page", page)
                .request()
                .get();
    }
}
