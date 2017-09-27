package com.bhe.measurement.server;

import com.bhe.measurement.server.db.DatabaseConfiguration;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Service;
import com.bhe.web.util.JsonResponseTransformer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static spark.Service.ignite;

public class Application {

    private static final int DEFAULT_PORT = 4567;

    private final DatabaseConfiguration dbConf;

    public static void main(String[] args) {
        new Application().init();
    }

    private Application() {
        dbConf = DatabaseConfiguration.fromEnv();
    }

    private void init() {
        Service http = ignite();

        http.port(getPort());

        http.get("/measurement", "application/json", this::getMeasurements, new JsonResponseTransformer());
        http.post("/measurement", "application/json", this::postMeasurement, new JsonResponseTransformer());
    }

    private int getPort() {
        return Optional
                .ofNullable(System.getenv("PORT"))
                .map(Integer::parseInt)
                .orElse(DEFAULT_PORT);
    }

    private Object getMeasurements(Request request, Response response) {
        String query = "select id, source, timestamp, type, value, unit from measurement";

        List<MeasurementBean> measurements = new ArrayList<>();

        withConnection(conn -> {
            try (Statement statement = conn.createStatement()) {
                ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    MeasurementBean bean = new MeasurementBean();
                    bean.setSource(rs.getString(2));
                    bean.setTimestampMillis(rs.getTimestamp(3).getTime());
                    bean.setType(rs.getString(4));
                    bean.setValue(rs.getDouble(5));
                    bean.setUnit(rs.getString(6));
                    measurements.add(bean);
                }
            } catch (SQLException e) {
                System.out.println("Error when creating statement");
                e.printStackTrace();
            }
        });


        return measurements;
    }

    private Object postMeasurement(Request request, Response response) {
        MeasurementBean measurement = MeasurementBean.fromJson(request.body());
        System.out.println("Inserting measurement");

        String insertString = "insert into measurement (source, timestamp, type, value, unit) " +
                "values (?, ?, ?, ?, ?)";

        withConnection(conn -> {
            try (PreparedStatement insertMeasurement = conn.prepareStatement(insertString)) {
                insertMeasurement.setString(1, measurement.getSource());
                insertMeasurement.setTimestamp(2, new Timestamp(measurement.getTimestampMillis()));
                insertMeasurement.setString(3, measurement.getType());
                insertMeasurement.setDouble(4, measurement.getValue());
                insertMeasurement.setString(5, measurement.getUnit());
                insertMeasurement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        System.out.println(LocalDateTime.now() + ": " + measurement.toString());
        response.type("application/json");
        response.status(HttpStatus.OK_200);
        return measurement;
    }

    private void withConnection(Consumer<Connection> consumer) {
        try (Connection conn = DriverManager.getConnection(
                dbConf.getUrl(),
                dbConf.getUser(),
                dbConf.getPassword()
        )) {
            System.out.println("Connected to database");
            consumer.accept(conn);
        } catch (SQLException e) {
            System.out.println("Failed to connect to database");
            e.printStackTrace();
        }
    }
}
