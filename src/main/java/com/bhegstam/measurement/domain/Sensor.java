package com.bhegstam.measurement.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class Sensor {
    private final SensorId id;
    private final String name;

    public static Sensor loadFromDb(SensorId sensorId, String name) {
        return new Sensor(sensorId, name);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
