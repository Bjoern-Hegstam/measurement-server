package com.bhegstam.measurement.domain;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SensorRegistration {
    private final SensorRegistrationId id;
    private final InstrumentationId instrumentationId;
    private final Sensor sensor;
    private final LocalDateTime validFromUtc;
    private final LocalDateTime validToUtc;

    public static SensorRegistration loadFromDb(SensorRegistrationId id, InstrumentationId instrumentationId, Sensor sensor, LocalDateTime validFromUtc, LocalDateTime validToUtc) {
        return new SensorRegistration(
                id,
                instrumentationId,
                sensor,
                validFromUtc,
                validToUtc
        );
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
