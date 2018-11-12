package com.bhegstam.measurement.port.rest.measurement;

import com.bhegstam.measurement.domain.Instrumentation;
import com.bhegstam.measurement.domain.Sensor;
import com.bhegstam.measurement.domain.SensorRegistration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class InstrumentationDto {
    @JsonProperty
    private final String id;

    @JsonProperty
    private final String name;

    @JsonProperty
    private final List<SensorRegistrationDto> sensorRegistrations;

    InstrumentationDto(Instrumentation instrumentation) {
        this.id = instrumentation.getId().getId();
        this.name = instrumentation.getName();
        this.sensorRegistrations = instrumentation
                .getRegistrations().stream()
                .map(SensorRegistrationDto::new)
                .collect(toList());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class SensorRegistrationDto {
        @JsonProperty
        private final SensorDto sensor;
        @JsonProperty
        private final String validFromUtc;
        @JsonProperty
        private final String validToUtc;

        SensorRegistrationDto(SensorRegistration sensorRegistration) {
            this.sensor = new SensorDto(sensorRegistration.getSensor());
            this.validFromUtc = sensorRegistration.getValidFromUtc().toString();
            this.validToUtc = sensorRegistration.getValidToUtc() != null ? sensorRegistration.getValidToUtc().toString() : null;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    private static class SensorDto {
        @JsonProperty
        private final String id;
        @JsonProperty
        private final String name;

        SensorDto(Sensor sensor) {
            this.id = sensor.getId().getId();
            this.name = sensor.getName();
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
