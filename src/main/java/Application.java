import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Service;
import util.JsonResponseTransformer;

import java.time.LocalDateTime;
import java.util.Optional;

import static spark.Service.ignite;

public class Application {

    private static final int DEFAULT_PORT = 4567;

    public static void main(String[] args) {
        new Application().init();
    }

    private void init() {
        Service http = ignite();

        http.port(getPort());

        http.post("/measurement", "application/json", this::postMeasurement, new JsonResponseTransformer());
    }

    private int getPort() {
        return Optional
                .ofNullable(System.getenv("PORT"))
                .map(Integer::parseInt)
                .orElse(DEFAULT_PORT);
    }

    private Object postMeasurement(Request request, Response response) {
        MeasurementBean measurement = MeasurementBean.fromJson(request.body());
        System.out.println(LocalDateTime.now() + ": " + measurement.toString());
        response.type("application/json");
        response.status(HttpStatus.OK_200);
        return measurement;
    }
}
