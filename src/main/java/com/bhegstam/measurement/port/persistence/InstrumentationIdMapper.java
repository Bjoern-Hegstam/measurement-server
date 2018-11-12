package com.bhegstam.measurement.port.persistence;

import com.bhegstam.measurement.domain.InstrumentationId;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstrumentationIdMapper implements RowMapper<InstrumentationId> {
    @Override
    public InstrumentationId map(ResultSet rs, StatementContext ctx) throws SQLException {
        return InstrumentationId.parse(rs.getString(1));
    }
}
