package com.bhe.measurement.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;

@Data
public class MeasurementBean {
    private String source;
    private long timestampMillis;
    private String type;
    private double value;
    private String unit;

    static MeasurementBean fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, MeasurementBean.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
