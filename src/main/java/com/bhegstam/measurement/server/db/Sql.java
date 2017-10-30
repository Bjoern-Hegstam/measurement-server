package com.bhegstam.measurement.server.db;

public class Sql {
    public static final String SELECT = "SELECT ";
    public static final String FROM = " FROM ";
    public static final String WHERE = " WHERE ";
    public static final String EQUALS = " = ";
    public static final String ORDER_BY = " ORDER BY ";
    public static final String DESC = " DESC ";
    public static final String LIMIT = " LIMIT ";
    public static final String OFFSET = " OFFSET ";

    public static String count(String column) {
        return String.format("count(%s)", column);
    }
}
