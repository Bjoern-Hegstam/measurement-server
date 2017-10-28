package com.bhegstam.measurement.server.measurement.api;

import com.bhegstam.measurement.server.measurement.db.DbMeasurementBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;

public class MeasurementBean {
    private String source;
    private long createdAtMillis;
    private String type;
    private double value;
    private String unit;

    static MeasurementBean fromJson(String json) {
        try {
            return new ObjectMapper()
                    .findAndRegisterModules()
                    .readValue(json, MeasurementBean.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getCreatedAtMillis() {
        return createdAtMillis;
    }

    public void setCreatedAtMillis(long createdAtMillis) {
        this.createdAtMillis = createdAtMillis;
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

    public DbMeasurementBean toDbBean() {
        DbMeasurementBean bean = new DbMeasurementBean();
        bean.setSource(source);
        bean.setCreatedAt(Instant.ofEpochMilli(createdAtMillis));
        bean.setType(type);
        bean.setValue(value);
        bean.setUnit(unit);
        return bean;
    }

    public static MeasurementBean fromDbBean(DbMeasurementBean dbBean) {
        MeasurementBean bean = new MeasurementBean();
        bean.setSource(dbBean.getSource());
        bean.setCreatedAtMillis(dbBean.getCreatedAt().toEpochMilli());
        bean.setType(dbBean.getType());
        bean.setValue(dbBean.getValue());
        bean.setUnit(dbBean.getUnit());
        return bean;
    }
}
