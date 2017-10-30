package com.bhegstam.measurement.server.db;

import java.util.ArrayList;
import java.util.List;

public class SqlBuilder {
    private final StringBuilder sb = new StringBuilder();
    private final List<Object> values = new ArrayList<>();

    public SqlBuilder append(String s) {
        sb.append(s);
        return this;
    }

    public SqlBuilder appendParametrizedValue(Object obj) {
        sb.append("?");
        values.add(obj);
        return this;
    }

    public SqlQueryData build() {
        return new SqlQueryData(sb.toString(), values);
    }
}
