package com.bhegstam.measurement.port.rest.measurement;

import com.bhegstam.measurement.domain.Measurement;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class GetMeasurementResponse {
    @JsonProperty
    private final String instrumentationId;

    @JsonProperty
    private final String sensorId;

    @JsonProperty
    private final long createdAtMillis;

    @JsonProperty
    private final String type;

    @JsonProperty
    private final double value;

    @JsonProperty
    private final String unit;

    GetMeasurementResponse(Measurement measurement) {
        this.instrumentationId = measurement.getInstrumentationId().getId();
        this.sensorId = measurement.getSensorId().getId();
        this.createdAtMillis = measurement.getCreatedAt().toEpochMilli();
        this.type = measurement.getType();
        this.value = measurement.getValue();
        this.unit = measurement.getUnit();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
