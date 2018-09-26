package com.bhegstam.measurement.port.rest.measurement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
public class CreateMeasurementRequest {
    @NotEmpty
    private final String source;

    private final long createdAtMillis;

    @NotEmpty
    private final String type;

    private final double value;

    @NotEmpty
    private final String unit;

    @JsonCreator
    public CreateMeasurementRequest(
            @JsonProperty(value = "source") String source,
            @JsonProperty(value = "createdAtMillis") long createdAtMillis,
            @JsonProperty(value = "type") String type,
            @JsonProperty(value = "value") double value,
            @JsonProperty(value = "unit") String unit
    ) {
        this.source = source;
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
