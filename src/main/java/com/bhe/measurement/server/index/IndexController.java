package com.bhe.measurement.server.index;

import com.bhe.measurement.server.util.Path;
import com.bhe.webutil.webapp.Controller;
import com.bhe.webutil.webapp.Request;
import com.bhe.webutil.webapp.Result;
import spark.Service;

import static com.bhe.webutil.webapp.ResultBuilder.result;
import static com.bhe.webutil.webapp.SparkWrappers.asSparkRoute;

public class IndexController implements Controller {

    @Override
    public void configureRoutes(Service http) {
        http.get(Path.Web.INDEX, asSparkRoute(this::getIndexPage));
    }

    private Result getIndexPage(Request request) {
        return result().render(Path.Template.INDEX);
    }
}
