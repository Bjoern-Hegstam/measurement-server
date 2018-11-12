package com.bhegstam.measurement.port.persistence;

import com.bhegstam.measurement.domain.Instrumentation;
import com.bhegstam.measurement.domain.InstrumentationId;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.util.Collections.emptyList;

public class InstrumentationMapper implements RowMapper<Instrumentation> {
    @Override
    public Instrumentation map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Instrumentation.loadFromDb(
                InstrumentationId.parse(rs.getString("id")),
                rs.getString("name"),
                emptyList()
        );
    }
}
