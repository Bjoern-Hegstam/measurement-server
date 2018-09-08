package com.bhegstam.measurement;

import com.bhegstam.measurement.application.MeasurementApplication;
import com.bhegstam.measurement.configuration.DbMigrationBundle;
import com.bhegstam.measurement.configuration.MeasurementServerApplicationConfiguration;
import com.bhegstam.measurement.port.persistence.RepositoryFactory;
import com.bhegstam.measurement.port.rest.measurement.MeasurementResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class MeasurementServerApplication extends Application<MeasurementServerApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new MeasurementServerApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<MeasurementServerApplicationConfiguration> bootstrap) {
        bootstrap.setObjectMapper(Jackson.newObjectMapper());
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
        bootstrap.addBundle(new DbMigrationBundle());
        bootstrap.addBundle(new AssetsBundle("/public", "/", "index.html")); // TODO: Ensure gzipped
    }

    @Override
    public void run(MeasurementServerApplicationConfiguration config, Environment environment) {
        RepositoryFactory repositoryFactory = new RepositoryFactory(environment, config.getDataSourceFactory());

        environment.jersey().register(new MeasurementResource(new MeasurementApplication(repositoryFactory.createMeasurementRepository())));

        configureCors(environment);
    }

    private void configureCors(Environment environment) {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Authorization,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}
