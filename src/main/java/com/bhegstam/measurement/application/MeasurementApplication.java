package com.bhegstam.measurement.application;

import com.bhegstam.measurement.db.PaginationSettings;
import com.bhegstam.measurement.db.QueryResult;
import com.bhegstam.measurement.domain.MeasurementSource;
import com.bhegstam.measurement.domain.Measurement;
import com.bhegstam.measurement.domain.MeasurementRepository;

import java.util.List;

public class MeasurementApplication {
    private final MeasurementRepository measurementRepository;

    public MeasurementApplication(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public List<MeasurementSource> getSources() {
        return measurementRepository.getSources();
    }

    public void addMeasurement(String sourceId, long createdAtMillis, String type, double value, String unit) {
        measurementRepository.addMeasurement(sourceId, createdAtMillis, type, value, unit);
    }

    public QueryResult<Measurement> getMeasurements(String sourceId, PaginationSettings paginationSettings) {
        return measurementRepository.getMeasurements(sourceId, paginationSettings);
    }
}
