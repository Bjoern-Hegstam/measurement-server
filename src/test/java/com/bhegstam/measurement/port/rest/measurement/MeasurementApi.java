package com.bhegstam.measurement.port.rest.measurement;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class MeasurementApi {
    private final WebTarget webTarget;

    public MeasurementApi(String serviceUrl) {
        webTarget = ClientBuilder
                .newClient()
                .target(serviceUrl);
    }

    Response getSources() {
        return webTarget
                .path("sources")
                .request()
                .get();
    }

    Response postMeasurement(String sourceId, String json) {
        return webTarget
                .path("sources")
                .path(sourceId)
                .path("measurements")
                .request()
                .post(Entity.json(json));
    }

    Response getMeasurements(String sourceId, int perPage, int page) {
        return webTarget
                .path("sources")
                .path(sourceId)
                .path("measurements")
                .queryParam("per_page", perPage)
                .queryParam("page", page)
                .request()
                .get();
    }
}
