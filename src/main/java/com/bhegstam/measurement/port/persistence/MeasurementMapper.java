package com.bhegstam.measurement.port.persistence;

import com.bhegstam.measurement.domain.InstrumentationId;
import com.bhegstam.measurement.domain.Measurement;
import com.bhegstam.measurement.domain.SensorId;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class MeasurementMapper implements RowMapper<Measurement> {
    @Override
    public Measurement map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Measurement.loadFromDb(
                InstrumentationId.parse(rs.getString("instrumentation_id")),
                SensorId.parse(rs.getString("sensor_id")),
                Instant.ofEpochMilli(rs.getTimestamp("timestamp").getTime()),
                rs.getString("type"),
                rs.getDouble("value"),
                rs.getString("unit")
        );
    }
}
