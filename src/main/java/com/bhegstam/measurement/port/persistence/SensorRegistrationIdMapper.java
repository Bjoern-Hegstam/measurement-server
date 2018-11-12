package com.bhegstam.measurement.port.persistence;

import com.bhegstam.measurement.domain.SensorRegistrationId;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SensorRegistrationIdMapper implements RowMapper<SensorRegistrationId> {
    @Override
    public SensorRegistrationId map(ResultSet rs, StatementContext ctx) throws SQLException {
        return SensorRegistrationId.parse(rs.getString(1));
    }
}
