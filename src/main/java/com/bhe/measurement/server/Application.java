package com.bhe.measurement.server;

import com.bhe.measurement.server.conf.ConfigurationModule;
import com.bhe.measurement.server.index.IndexController;
import com.bhe.measurement.server.measurement.MeasurementApiController;
import com.bhe.webutil.webapp.ApplicationBase;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import spark.Service;
import java.util.Optional;

import static java.util.Collections.singletonList;

public class Application extends ApplicationBase {

    private static final int DEFAULT_PORT = 4567;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new ConfigurationModule()
        );

        injector
                .getInstance(Application.class)
                .init();
    }

    @Inject
    private Application(
            IndexController indexController,
            MeasurementApiController measurementApiController
    ) {
        super(
                singletonList(indexController),
                singletonList(measurementApiController),
                false
        );
    }

    @Override
    protected void configureServer(Service http) {
        http.port(getPort());
    }

    private int getPort() {
        return Optional
                .ofNullable(System.getenv("PORT"))
                .map(Integer::parseInt)
                .orElse(DEFAULT_PORT);
    }
}
