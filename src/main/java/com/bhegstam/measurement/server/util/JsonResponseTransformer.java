package com.bhegstam.measurement.server.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import spark.ResponseTransformer;

public class JsonResponseTransformer implements ResponseTransformer {
    @Override
    public String render(Object o) throws Exception {
        return new ObjectMapper()
                .findAndRegisterModules()
                .writeValueAsString(o);
    }
}
