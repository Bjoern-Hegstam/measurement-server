package com.bhegstam.measurement.port.rest.measurement;

import com.bhegstam.measurement.domain.Measurement;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class GetMeasurementResponse {
    @JsonProperty
    private SourceDto source;

    @JsonProperty
    private long createdAtMillis;

    @JsonProperty
    private String type;

    @JsonProperty
    private double value;

    @JsonProperty
    private String unit;

    public GetMeasurementResponse(Measurement measurement) {
        this.source = new SourceDto(measurement.getSource());
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
