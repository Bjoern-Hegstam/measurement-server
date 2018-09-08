package com.bhegstam.measurement.util;

import com.bhegstam.measurement.configuration.DbMigrationBundle;
import com.bhegstam.measurement.configuration.MeasurementServerApplicationConfiguration;
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
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;

public class TestDatabaseSetup implements TestRule {
    private static final List<String> CLEANUP_SQL_STATEMENTS = asList(
            "delete from measurement"
    );

    private final RepositoryFactory repositoryFactory;
    private final ManagedDataSource dataSource;

    public TestDatabaseSetup() {
        this(null);
    }

    public TestDatabaseSetup(MeasurementServerApplicationConfiguration config) {
        if (config == null) {
            config = loadConfiguration();
        }

        Environment environment = new Environment("test-env", Jackson.newObjectMapper(), null, new MetricRegistry(), null);

        new DbMigrationBundle().run(config, environment);

        DataSourceFactory dataSourceFactory = config.getDataSourceFactory();
        repositoryFactory = new RepositoryFactory(environment, dataSourceFactory);
        dataSource = dataSourceFactory.build(new MetricRegistry(), "cleanup");
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before();
                try {
                    base.evaluate();
                } finally {
                    after();
                }
            }
        };
    }

    private void before() {
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

    private void after() {
    }

    public RepositoryFactory getRepositoryFactory() {
        return repositoryFactory;
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
}
