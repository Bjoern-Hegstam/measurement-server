package com.bhegstam.measurement.port.persistence;

import com.bhegstam.measurement.domain.MeasurementRepository;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;

public class RepositoryFactory {
    private final Jdbi jdbi;

    public RepositoryFactory(Environment environment, DataSourceFactory dataSourceFactory) {
        jdbi = new JdbiFactory().build(environment, dataSourceFactory, "datasource");
    }

    public Jdbi getJdbi() {
        return jdbi;
    }

    public MeasurementRepository createMeasurementRepository() {
        return jdbi.onDemand(JdbiMeasurementRepository.class);
    }
}
