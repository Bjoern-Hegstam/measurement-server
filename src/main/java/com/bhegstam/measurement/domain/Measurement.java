package com.bhegstam.measurement.domain;

import lombok.Getter;

import java.time.Instant;

@Getter
public class Measurement {
    private String source;
    private Instant createdAt;
    private String type;
    private double value;
    private String unit;

    public Measurement(String source, Instant createdAt, String type, double value, String unit) {
        this.source = source;
        this.createdAt = createdAt;
        this.type = type;
        this.value = value;
        this.unit = unit;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "source='" + source + '\'' +
                ", createdAt=" + createdAt +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                '}';
    }
}
