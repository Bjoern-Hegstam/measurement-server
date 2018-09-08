package com.bhegstam.measurement.port.rest.measurement;

import com.bhegstam.measurement.domain.MeasurementSource;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class SourceDto {
    @JsonProperty
    private final String name;

    SourceDto(MeasurementSource source) {
        this.name = source.getName();
    }

    SourceDto(String sourceId) {
        this.name = sourceId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
