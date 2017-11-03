package com.bhegstam.measurement.server.measurement.api;

public class MeasurementSource {
    private final String name;

    public MeasurementSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
