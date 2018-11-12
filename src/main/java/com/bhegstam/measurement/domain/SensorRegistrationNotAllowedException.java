package com.bhegstam.measurement.domain;

import java.time.Instant;

public class SensorRegistrationNotAllowedException extends RuntimeException {
    private final InstrumentationId instrumentationId;
    private final SensorId sensorId;
    private final Instant validFrom;

    public SensorRegistrationNotAllowedException(InstrumentationId instrumentationId, SensorId sensorId, Instant validFrom) {
        this.instrumentationId = instrumentationId;
        this.sensorId = sensorId;
        this.validFrom = validFrom;
    }
}
