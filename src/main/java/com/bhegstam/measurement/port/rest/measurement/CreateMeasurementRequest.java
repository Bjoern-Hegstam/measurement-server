package com.bhegstam.measurement.port.rest.measurement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
public class CreateMeasurementRequest {
    private long createdAtMillis;

    @NotEmpty
    private String type;

    private double value;

    @NotEmpty
    private String unit;

    @JsonCreator
    public CreateMeasurementRequest(
            @JsonProperty(value = "createdAtMillis", required = true) long createdAtMillis,
            @JsonProperty(value = "type", required = true) String type,
            @JsonProperty(value = "value", required = true) double value,
            @JsonProperty(value = "unit", required = true) String unit
    ) {
        this.createdAtMillis = createdAtMillis;
        this.type = type;
        this.value = value;
        this.unit = unit;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
