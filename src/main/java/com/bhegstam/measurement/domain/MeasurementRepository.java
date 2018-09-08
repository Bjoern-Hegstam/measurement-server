package com.bhegstam.measurement.domain;

import com.bhegstam.measurement.db.PaginationSettings;
import com.bhegstam.measurement.db.QueryResult;

import java.time.Instant;
import java.util.List;

public interface MeasurementRepository {
    List<MeasurementSource> getSources();

    void addMeasurement(String sourceId, Instant createdAt, String type, double value, String unit);

    QueryResult<Measurement> getMeasurements(String sourceId, PaginationSettings paginationSettings);
}
