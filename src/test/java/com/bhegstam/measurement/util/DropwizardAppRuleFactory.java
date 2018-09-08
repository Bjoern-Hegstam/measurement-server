package com.bhegstam.measurement.util;

import com.bhegstam.measurement.MeasurementServerApplication;
import com.bhegstam.measurement.configuration.MeasurementServerApplicationConfiguration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

public class DropwizardAppRuleFactory {
    public static DropwizardAppRule<MeasurementServerApplicationConfiguration> forIntegrationTest() {
        return new DropwizardAppRule<>(
                MeasurementServerApplication.class,
                ResourceHelpers.resourceFilePath(TestData.getTestConfigFilename())
        );
    }
}
