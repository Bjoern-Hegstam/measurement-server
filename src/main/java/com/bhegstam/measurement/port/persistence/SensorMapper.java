package com.bhegstam.measurement.port.persistence;

import com.bhegstam.measurement.domain.Sensor;
import com.bhegstam.measurement.domain.SensorId;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SensorMapper implements RowMapper<Sensor> {
    @Override
    public Sensor map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Sensor.loadFromDb(
                SensorId.parse(rs.getString("id")),
                rs.getString("name")
        );
    }
}
