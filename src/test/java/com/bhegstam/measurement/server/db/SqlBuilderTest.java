package com.bhegstam.measurement.server.db;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SqlBuilderTest {

    @Test
    public void simple_query() {
        // given
        SqlQueryData queryData = new SqlBuilder()
                .append(Sql.SELECT)
                .append("A, B, C")
                .append(Sql.FROM)
                .append("table")
                .append(Sql.WHERE)
                .append("A")
                .append(Sql.EQUALS)
                .appendParametrizedValue(42)
                .build();

        // then
        String expectedQuery = "SELECT A, B, C FROM table WHERE A = ?";
        assertEquals(expectedQuery, queryData.getQuery());
        assertEquals(1, queryData.getValues().size());
        assertEquals(42, queryData.getValues().get(0));
    }
}