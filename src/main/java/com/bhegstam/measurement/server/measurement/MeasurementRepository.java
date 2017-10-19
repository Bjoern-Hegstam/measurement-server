package com.bhegstam.measurement.server.measurement;

import com.bhegstam.measurement.server.db.DatabaseConfiguration;
import com.google.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MeasurementRepository {
    private final DatabaseConfiguration dbConf;

    @Inject
    public MeasurementRepository(DatabaseConfiguration dbConf) {
        this.dbConf = dbConf;
    }

    public List<MeasurementBean> getAll() {
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
                // TODO: Better error handling
                e.printStackTrace();
            }
        });


        return measurements;
    }

    public MeasurementBean create(MeasurementBean measurement) {
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
            // TODO: Better error handling
            e.printStackTrace();
        }
    }
}
