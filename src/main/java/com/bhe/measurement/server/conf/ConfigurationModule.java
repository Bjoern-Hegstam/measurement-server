package com.bhe.measurement.server.conf;

import com.bhe.measurement.server.db.DatabaseConfiguration;
import com.google.inject.AbstractModule;

public class ConfigurationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DatabaseConfiguration.class)
                .toProvider(DatabaseConfiguration::fromEnv)
                .asEagerSingleton();
    }
}
