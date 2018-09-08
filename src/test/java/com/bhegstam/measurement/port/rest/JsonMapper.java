package com.bhegstam.measurement.port.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.Response;
import java.io.IOException;

public class JsonMapper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public JsonNode read(Response response) {
        try {
            return OBJECT_MAPPER.readTree(response.readEntity(String.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
