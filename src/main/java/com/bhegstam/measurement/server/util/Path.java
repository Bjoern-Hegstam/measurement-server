package com.bhegstam.measurement.server.util;

public class Path {
    public static class Web {
        public static final String INDEX = "/";
    }

    public static class Template {
        public static final String INDEX = "/velocity/index.vm";
    }

    public static class Api {
        public static final String SOURCES = "/sources";
        public static final String MEASUREMENTS = "/measurements";
    }
}
