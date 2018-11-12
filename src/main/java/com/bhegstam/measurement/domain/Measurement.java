package com.bhegstam.measurement.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.time.Instant;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Measurement {
    private final InstrumentationId instrumentationId;
    private final SensorId sensorId;
    private final Instant createdAt;
    private final String type;
    private final double value;
    private final String unit;

    public static Measurement loadFromDb(InstrumentationId instrumentationId, SensorId sensorId, Instant createdAt, String type, double value, String unit) {
        return new Measurement(
                instrumentationId,
                sensorId,
                createdAt,
                type,
                value,
                unit
        );
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
