import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Service;
import util.JsonResponseTransformer;

import java.time.LocalDateTime;

import static spark.Service.ignite;

public class Application {
    public static void main(String[] args) {
        new Application().init();
    }

    private void init() {
        Service http = ignite();

        http.port(4567);

        http.post("/measurement", "application/json", this::postMeasurement, new JsonResponseTransformer());
    }

    private Object postMeasurement(Request request, Response response) {
        MeasurementBean measurement = MeasurementBean.fromJson(request.body());
        System.out.println(LocalDateTime.now() + ": " + measurement.toString());
        response.type("application/json");
        response.status(HttpStatus.OK_200);
        return measurement;
    }
}
