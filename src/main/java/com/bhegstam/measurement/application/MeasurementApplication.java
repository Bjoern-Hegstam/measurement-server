package com.bhegstam.measurement.application;

import com.bhegstam.measurement.db.PaginationSettings;
import com.bhegstam.measurement.db.QueryResult;
import com.bhegstam.measurement.domain.*;

import java.time.Instant;
import java.util.List;

public class MeasurementApplication {
    private final MeasurementRepository measurementRepository;

    public MeasurementApplication(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public List<Instrumentation> getInstrumentations() {
        return measurementRepository.getInstrumentations();
    }

    public void addMeasurement(String source, Instant createdAt, String type, double value, String unit) {
        measurementRepository.addMeasurement(source, createdAt, type, value, unit);
    }

    public QueryResult<Measurement> getMeasurements(InstrumentationId instrumentationId, SensorId sensorId, PaginationSettings paginationSettings) {
        return measurementRepository.getMeasurements(instrumentationId, sensorId, paginationSettings);
    }
}
