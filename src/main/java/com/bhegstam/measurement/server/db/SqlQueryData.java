package com.bhegstam.measurement.server.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SqlQueryData {
    private final String query;
    private final List<Object> values;

    public SqlQueryData(String query, List<Object> values) {
        this.query = query;
        this.values = values;
    }

    public String getQuery() {
        return query;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(PreparedStatement statement) throws SQLException {
        for (int i = 0; i < values.size(); i++) {
            statement.setObject(i + 1, values.get(i));
        }
    }

    @Override
    public String toString() {
        return "SqlQueryData{" +
                "query='" + query + '\'' +
                ", values=" + values +
                '}';
    }
}
