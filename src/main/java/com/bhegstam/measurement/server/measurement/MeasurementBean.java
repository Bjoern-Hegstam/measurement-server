package com.bhegstam.measurement.server.measurement;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.time.Instant;

@Data
public class MeasurementBean {
    private String source;
    private Instant createdAt;
    private String type;
    private double value;
    private String unit;

    static MeasurementBean fromJson(String json) {
        try {
            return new ObjectMapper()
                    .findAndRegisterModules()
                    .readValue(json, MeasurementBean.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
