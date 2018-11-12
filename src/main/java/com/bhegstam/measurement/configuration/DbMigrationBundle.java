package com.bhegstam.measurement.configuration;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;

public class DbMigrationBundle implements ConfiguredBundle<MeasurementServerApplicationConfiguration> {
    @Override
    public void run(MeasurementServerApplicationConfiguration config, Environment environment) {
        DataSourceFactory dataSourceFactory = config.getDataSourceFactory();
        Flyway.configure()
              .dataSource(dataSourceFactory.getUrl(), dataSourceFactory.getUser(), dataSourceFactory.getPassword())
              .load()
              .migrate();
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }
}
