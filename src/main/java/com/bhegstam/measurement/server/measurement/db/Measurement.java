package com.bhegstam.measurement.server.measurement.db;

import java.time.Instant;

public class Measurement {
    static class DB {
        static final String TABLE_NAME = "measurement";
        static final String ID = "id";
        static final String SOURCE = "source";
        static final String TIMESTAMP = "timestamp";
        static final String TYPE = "type";
        static final String VALUE = "value";
        static final String UNIT = "unit";
    }

    private String source;
    private Instant createdAt;
    private String type;
    private double value;
    private String unit;

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
