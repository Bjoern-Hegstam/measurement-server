package com.bhegstam.measurement.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Instrumentation {
    private final InstrumentationId id;
    private final String name;
    private List<SensorRegistration> registrations;

    public static Instrumentation loadFromDb(InstrumentationId id, String name, List<SensorRegistration> sensorRegistrations) {
        return new Instrumentation(id, name, sensorRegistrations);
    }

    public void setRegistrations(List<SensorRegistration> registrations) {
        this.registrations = registrations;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
