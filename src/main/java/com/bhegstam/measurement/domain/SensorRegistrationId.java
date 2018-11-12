package com.bhegstam.measurement.domain;

public class SensorRegistrationId extends Identifier {
    public SensorRegistrationId() {
    }

    public SensorRegistrationId(String id) {
        super(id);
    }

    public static SensorRegistrationId parse(String id) {
        return new SensorRegistrationId(id);
    }
}
