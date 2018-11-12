package com.bhegstam.measurement.domain;

public class SensorId extends Identifier {
    public SensorId() {
    }

    private SensorId(String id) {
        super(id);
    }

    public static SensorId parse(String id) {
        return new SensorId(id);
    }
}
