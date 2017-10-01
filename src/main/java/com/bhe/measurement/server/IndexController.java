package com.bhe.measurement.server;

import com.bhe.webutil.webapp.Controller;
import com.bhe.webutil.webapp.Request;
import com.bhe.webutil.webapp.Result;
import spark.Service;

import static com.bhe.webutil.webapp.ResultBuilder.result;
import static com.bhe.webutil.webapp.SparkWrappers.asSparkRoute;

public class IndexController implements Controller {
    @Override
    public void configureRoutes(Service http) {
        http.get(Path.Web.INDEX, asSparkRoute(this::getFrontPage));
    }

    private Result getFrontPage(Request request) {
        return result().returnPayload("Hello World");
    }
}
