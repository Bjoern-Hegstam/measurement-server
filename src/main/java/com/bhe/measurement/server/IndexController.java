package com.bhe.measurement.server;

import com.bhe.webutil.webapp.Controller;
import com.bhe.webutil.webapp.Request;
import com.bhe.webutil.webapp.Result;
import com.google.inject.Inject;
import spark.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bhe.webutil.webapp.ResultBuilder.result;
import static com.bhe.webutil.webapp.SparkWrappers.asSparkRoute;

public class IndexController implements Controller {
    private final MeasurementRepository measurementRepository;

    @Inject
    public IndexController(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Override
    public void configureRoutes(Service http) {
        http.get(Path.Web.INDEX, asSparkRoute(this::getFrontPage));
    }

    private Result getFrontPage(Request request) {
        List<MeasurementBean> measurements = measurementRepository.getAll();

        Map<String, Object> model = new HashMap<>();
        model.put("measurements", measurements);

        return result().render(Path.Template.INDEX, model);
    }
}
