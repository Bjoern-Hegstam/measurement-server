package com.bhegstam.measurement.port.rest;

import org.hamcrest.Matchers;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertThat;

public class ResponseTestUtil {
    static final int UNPROCESSABLE_ENTITY = 422;

    public static void assertResponseStatus(Response response, Response.Status status) {
        assertThat(response.getStatus(), Matchers.is(status.getStatusCode()));
    }

    public static void assertResponseStatus(Response response, int statusCode) {
        assertThat(response.getStatus(), Matchers.is(statusCode));
    }
}
