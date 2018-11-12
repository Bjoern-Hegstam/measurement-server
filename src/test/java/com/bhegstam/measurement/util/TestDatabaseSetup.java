package com.bhegstam.measurement.util;

import com.bhegstam.measurement.configuration.DbMigrationBundle;
import com.bhegstam.measurement.configuration.MeasurementServerApplicationConfiguration;
import com.bhegstam.measurement.domain.MeasurementRepository;
import com.bhegstam.measurement.port.persistence.RepositoryFactory;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.validation.BaseValidator;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class TestDatabaseSetup implements BeforeEachCallback, TestInstancePostProcessor {
    private static final List<String> CLEANUP_SQL_STATEMENTS = asList(
            "delete from measurement",
            "delete from sensor_registration",
            "delete from sensor",
            "delete from instrumentation"
    );

    private final ManagedDataSource dataSource;
    private Jdbi jdbi;
    private MeasurementRepository measurementRepository;

    public TestDatabaseSetup() {
        this(null);
    }

    private TestDatabaseSetup(MeasurementServerApplicationConfiguration config) {
        if (config == null) {
            config = loadConfiguration();
        }

        Environment environment = new Environment("test-env", Jackson.newObjectMapper(), null, new MetricRegistry(), null);

        new DbMigrationBundle().run(config, environment);

        DataSourceFactory dataSourceFactory = config.getDataSourceFactory();
        RepositoryFactory repositoryFactory = new RepositoryFactory(environment, dataSourceFactory);
        jdbi = repositoryFactory.getJdbi();
        measurementRepository = repositoryFactory.createMeasurementRepository();
        dataSource = dataSourceFactory.build(new MetricRegistry(), "cleanup");
    }

    private MeasurementServerApplicationConfiguration loadConfiguration() {
        try {
            return new YamlConfigurationFactory<>(
                    MeasurementServerApplicationConfiguration.class,
                    BaseValidator.newValidator(),
                    Jackson.newObjectMapper(new YAMLFactory()),
                    ""
            ).build(new FileConfigurationSourceProvider(), ResourceHelpers.resourceFilePath(TestData.getTestConfigFilename()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        emptyDb();
    }

    private void emptyDb() {
        try (Connection conn = dataSource.getConnection()) {
            for (String statement : CLEANUP_SQL_STATEMENTS) {
                conn.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws IllegalAccessException {
        Field[] fields = testInstance.getClass().getDeclaredFields();
        Map<Class<?>, Object> injectable = Map.of(
                Jdbi.class, jdbi,
                MeasurementRepository.class, measurementRepository
        );

        for (Field field : fields) {
            if (!field.isAnnotationPresent(Inject.class)) {
                continue;
            }

            if (injectable.containsKey(field.getType())) {
                field.setAccessible(true);
                field.set(testInstance, injectable.get(field.getType()));
            }
        }
    }
}
