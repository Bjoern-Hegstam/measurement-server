package com.bhegstam.measurement.util;

import java.util.Optional;

public class TestData {
    private static final String CONF_FILENAME = "CONF_FILENAME";
    private static final String DEFAULT_CONF_FILENAME = "test-config.yml";

    public static String getTestConfigFilename() {
        return Optional
                .ofNullable(System.getenv(CONF_FILENAME))
                .orElse(DEFAULT_CONF_FILENAME);
    }
}
