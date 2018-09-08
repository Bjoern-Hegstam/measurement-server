package com.bhegstam.measurement.port.persistence;

import com.bhegstam.measurement.domain.MeasurementSource;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MeasurementSourceMapper implements RowMapper<MeasurementSource> {
    @Override
    public MeasurementSource map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new MeasurementSource(
                rs.getString("source")
        );
    }
}
