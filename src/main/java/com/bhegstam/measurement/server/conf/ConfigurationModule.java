package com.bhegstam.measurement.server.conf;

import com.bhegstam.measurement.server.db.DatabaseConfiguration;
import com.google.inject.AbstractModule;

public class ConfigurationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DatabaseConfiguration.class)
                .toProvider(DatabaseConfiguration::fromEnv)
                .asEagerSingleton();
    }
}
