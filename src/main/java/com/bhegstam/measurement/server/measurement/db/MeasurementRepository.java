package com.bhegstam.measurement.server.measurement.db;

import com.bhegstam.measurement.server.db.*;
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

    public QueryResult<Measurement> find(PaginationSettings paginationSettings) {
        return find(null, paginationSettings);
    }

    public QueryResult<Measurement> find(String source, PaginationSettings paginationSettings) {
        logger.debug("Finding measurements");

        List<Measurement> measurements = new ArrayList<>();
        List<PaginationInformation> pageInfoContainer = new ArrayList<>();

        withConnection(conn -> {
            SqlQueryData totalCountQueryData = new SqlBuilder()
                    .append(Sql.SELECT)
                    .append(Sql.count(Measurement.DB.ID))
                    .append(Sql.FROM)
                    .append(Measurement.DB.TABLE_NAME)
                    .build();
            logger.debug(totalCountQueryData);

            final int totalCount;
            try (Statement statement = conn.createStatement()) {
                ResultSet rs = statement.executeQuery(totalCountQueryData.getQuery());
                rs.next();
                totalCount = rs.getInt(1);
                logger.debug("Measurement count (total): " + totalCount);
            } catch (SQLException e) {
                logger.error("Error when creating statement", e);
                return;
            }

            PaginationInformation paginationInformation = PaginationInformation.calculate(totalCount, paginationSettings);
            pageInfoContainer.add(paginationInformation);
            logger.debug(paginationInformation);

            SqlBuilder sqlBuilder = new SqlBuilder();
            sqlBuilder
                    .append(Sql.SELECT)
                    .append(Measurement.DB.ID).append(", ")
                    .append(Measurement.DB.SOURCE).append(", ")
                    .append(Measurement.DB.TIMESTAMP).append(", ")
                    .append(Measurement.DB.TYPE).append(", ")
                    .append(Measurement.DB.VALUE).append(", ")
                    .append(Measurement.DB.UNIT);

            sqlBuilder.append(Sql.FROM)
                      .append(Measurement.DB.TABLE_NAME);

            if (source != null) {
                sqlBuilder.append(Sql.WHERE)
                          .append(Measurement.DB.SOURCE)
                          .append(Sql.EQUALS)
                          .appendParametrizedValue(source);
            }

            sqlBuilder.append(Sql.ORDER_BY)
                      .append(Measurement.DB.TIMESTAMP)
                      .append(Sql.DESC);

            sqlBuilder.append(Sql.LIMIT)
                      .appendParametrizedValue(paginationInformation.getPerPage())
                      .append(Sql.OFFSET)
                      .appendParametrizedValue(paginationInformation.getOffset());

            SqlQueryData selectMeasurementsQueryData = sqlBuilder.build();
            logger.debug(selectMeasurementsQueryData);

            try (PreparedStatement statement = conn.prepareStatement(selectMeasurementsQueryData.getQuery())) {
                selectMeasurementsQueryData.setValues(statement);

                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    Measurement bean = new Measurement();
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

        return new QueryResult<>(measurements, pageInfoContainer.get(0));
    }

    public Measurement create(Measurement measurement) {
        logger.debug("Creating measurement");
        logger.debug(measurement);

        SqlQueryData insertQueryData = new SqlBuilder()
                .append(Sql.INSERT_INTO)
                .append(Measurement.DB.TABLE_NAME)
                .append(" (")
                .append(Measurement.DB.SOURCE).append(", ")
                .append(Measurement.DB.TIMESTAMP).append(", ")
                .append(Measurement.DB.TYPE).append(", ")
                .append(Measurement.DB.VALUE).append(", ")
                .append(Measurement.DB.UNIT)
                .append(")")
                .append(Sql.VALUES)
                .append("(")
                .appendParametrizedValue(measurement.getSource()).append(", ")
                .appendParametrizedValue(new Timestamp(measurement.getCreatedAt().toEpochMilli())).append(", ")
                .appendParametrizedValue(measurement.getType()).append(", ")
                .appendParametrizedValue(measurement.getValue()).append(", ")
                .appendParametrizedValue(measurement.getUnit())
                .append(")")
                .build();

        logger.debug(insertQueryData);

        withConnection(conn -> {
            try (PreparedStatement insertMeasurement = conn.prepareStatement(insertQueryData.getQuery())) {
                insertQueryData.setValues(insertMeasurement);
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
            consumer.accept(conn);
        } catch (SQLException e) {
            logger.error("Failed to connect to database", e);
        }
    }
}
