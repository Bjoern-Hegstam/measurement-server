package com.bhegstam.measurement.server.measurement.db;

import com.bhegstam.measurement.server.db.DatabaseConfiguration;
import com.bhegstam.measurement.server.db.PaginationInformation;
import com.bhegstam.measurement.server.db.PaginationSettings;
import com.bhegstam.measurement.server.logging.InjectLogger;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MeasurementRepository {
    @InjectLogger
    private Logger logger;

    private final DatabaseConfiguration dbConf;

    @Inject
    public MeasurementRepository(DatabaseConfiguration dbConf) {
        this.dbConf = dbConf;
    }

    public List<DbMeasurementBean> find(PaginationSettings paginationSettings) {
        logger.debug("Finding measurements");


        List<DbMeasurementBean> measurements = new ArrayList<>();

        withConnection(conn -> {
            String totalCountQuery = "SELECT count(id) FROM measurement";
            final int totalCount;

            try (Statement statement = conn.createStatement()) {
                ResultSet rs = statement.executeQuery(totalCountQuery);
                rs.next();
                totalCount = rs.getInt(1);
            } catch (SQLException e) {
                logger.error("Error when creating statement", e);
                return;
            }

            PaginationInformation paginationInformation = PaginationInformation.calculate(totalCount, paginationSettings);
            logger.debug(paginationInformation);

            String query = "SELECT id, source, timestamp, type, value, unit " +
                    "FROM measurement " +
                    "ORDER BY timestamp DESC " +
                    "LIMIT ? OFFSET ?";

            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, paginationInformation.getPerPage());
                statement.setInt(2, paginationInformation.getOffset());
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    DbMeasurementBean bean = new DbMeasurementBean();
                    bean.setSource(rs.getString(2));
                    bean.setCreatedAt(Instant.ofEpochMilli(rs.getTimestamp(3).getTime()));
                    bean.setType(rs.getString(4));
                    bean.setValue(rs.getDouble(5));
                    bean.setUnit(rs.getString(6));
                    measurements.add(bean);
                }
            } catch (SQLException e) {
                logger.error("Error when creating statement", e);
            }
        });

        return measurements;
    }

    public DbMeasurementBean create(DbMeasurementBean measurement) {
        logger.debug("Creating measurement");
        logger.debug(measurement);

        String insertString = "INSERT INTO measurement (source, timestamp, type, value, unit) VALUES (?, ?, ?, ?, ?)";

        withConnection(conn -> {
            try (PreparedStatement insertMeasurement = conn.prepareStatement(insertString)) {
                insertMeasurement.setString(1, measurement.getSource());
                insertMeasurement.setTimestamp(2, new Timestamp(measurement.getCreatedAt().toEpochMilli()));
                insertMeasurement.setString(3, measurement.getType());
                insertMeasurement.setDouble(4, measurement.getValue());
                insertMeasurement.setString(5, measurement.getUnit());
                insertMeasurement.executeUpdate();
            } catch (SQLException e) {
                logger.error("Error when creating insert statement", e);
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
            logger.debug("Database connection established");
            consumer.accept(conn);
        } catch (SQLException e) {
            logger.error("Failed to connect to database", e);
        }
    }
}
