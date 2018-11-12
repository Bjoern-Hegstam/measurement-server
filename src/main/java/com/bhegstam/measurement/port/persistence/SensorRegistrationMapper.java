package com.bhegstam.measurement.port.persistence;

import com.bhegstam.measurement.domain.*;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;

public class SensorRegistrationMapper implements RowMapper<SensorRegistration> {
    @Override
    public SensorRegistration map(ResultSet rs, StatementContext ctx) throws SQLException {
        Timestamp validTo = rs.getTimestamp("sr_valid_to");
        return SensorRegistration.loadFromDb(
                SensorRegistrationId.parse(rs.getString("sr_id")),
                InstrumentationId.parse(rs.getString("sr_instrumentation_id")),
                Sensor.loadFromDb(
                        SensorId.parse(rs.getString("s_id")),
                        rs.getString("s_name")
                ),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(rs.getTimestamp("sr_valid_from").getTime()), UTC),
                validTo != null
                        ? LocalDateTime.ofInstant(Instant.ofEpochMilli(validTo.getTime()), UTC)
                        : null
        );
    }
}
