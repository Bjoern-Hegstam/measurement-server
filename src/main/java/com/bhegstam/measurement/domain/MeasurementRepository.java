package com.bhegstam.measurement.domain;

import com.bhegstam.measurement.db.PaginationSettings;
import com.bhegstam.measurement.db.QueryResult;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface MeasurementRepository {
    void addInstrumentation(InstrumentationId instrumentationId, String name);

    List<Instrumentation> getInstrumentations();

    void addSensor(Sensor sensor);

    void registerSensor(InstrumentationId instrumentationId, SensorId sensorId, LocalDateTime validFromUtc);

    void unregisterSensor(InstrumentationId instrumentationId, SensorId sensorId, LocalDateTime validToUtc);

    boolean addMeasurement(String source, Instant createdAt, String type, double value, String unit);

    QueryResult<Measurement> getMeasurements(InstrumentationId instrumentationId, SensorId sensorId, PaginationSettings paginationSettings);
}
