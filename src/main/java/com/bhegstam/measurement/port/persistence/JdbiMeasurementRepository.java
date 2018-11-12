package com.bhegstam.measurement.port.persistence;

import com.bhegstam.measurement.db.PaginationInformation;
import com.bhegstam.measurement.db.PaginationSettings;
import com.bhegstam.measurement.db.QueryResult;
import com.bhegstam.measurement.domain.*;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

@RegisterRowMapper(InstrumentationMapper.class)
@RegisterRowMapper(InstrumentationIdMapper.class)
@RegisterRowMapper(SensorMapper.class)
@RegisterRowMapper(SensorRegistrationMapper.class)
@RegisterRowMapper(SensorRegistrationIdMapper.class)
@RegisterRowMapper(MeasurementMapper.class)
public interface JdbiMeasurementRepository extends MeasurementRepository {
    Logger LOGGER = LoggerFactory.getLogger(JdbiMeasurementRepository.class);

    @SqlUpdate("INSERT INTO instrumentation(id, name) VALUES (:instrumentationId.id, :name)")
    void addInstrumentation(@BindBean("instrumentationId") InstrumentationId instrumentationId, @Bind("name") String name);

    @Transaction
    default List<Instrumentation> getInstrumentations() {
        List<Instrumentation> instrumentations = getInstrumentationEntities();
        for (Instrumentation instrumentation : instrumentations) {
            List<SensorRegistration> registrations = getSensorRegistrations(instrumentation.getId());
            instrumentation.setRegistrations(registrations);
        }
        return instrumentations;
    }

    @SqlQuery("SELECT * FROM instrumentation ORDER BY name")
    List<Instrumentation> getInstrumentationEntities();

    @SqlQuery("SELECT " +
            "sr.id as sr_id " +
            ",sr.instrumentation_id as sr_instrumentation_id " +
            ",sr.valid_from as sr_valid_from " +
            ",sr.valid_to as sr_valid_to " +
            ",s.id as s_id " +
            ",s.name as s_name " +
            "FROM sensor_registration sr " +
            "INNER JOIN sensor s on s.id = sr.sensor_id " +
            "WHERE sr.instrumentation_id = :instrumentationId.id " +
            "ORDER BY sr.valid_to DESC NULLS FIRST, sr.valid_from DESC")
    List<SensorRegistration> getSensorRegistrations(@BindBean("instrumentationId") InstrumentationId instrumentationId);

    default void addSensor(Sensor sensor) {
        addSensor(sensor.getId(), sensor.getName());
    }

    @SqlUpdate("INSERT INTO sensor(id, name) VALUES (:sensorId.id, :name)")
    void addSensor(@BindBean("sensorId") SensorId sensorId, @Bind("name") String name);

    @Transaction
    default void registerSensor(InstrumentationId instrumentationId, SensorId sensorId, LocalDateTime validFromUtc) {
        Instant validFromInstant = validFromUtc.toInstant(UTC);
        if (findSensorRegistrationValidAtOrAfter(instrumentationId, sensorId, validFromInstant).isPresent()) {
            throw new SensorRegistrationNotAllowedException(instrumentationId, sensorId, validFromInstant);
        }

        SensorRegistrationId registrationId = new SensorRegistrationId();
        registerSensor_internal(
                registrationId,
                instrumentationId,
                sensorId,
                validFromInstant
        );
    }

    @SqlQuery("SELECT id " +
            "FROM sensor_registration " +
            "WHERE instrumentation_id = :instrumentationId.id " +
            "AND sensor_id = :sensorId.id " +
            "AND (" +
            "   (valid_from <= :timestamp AND (valid_to IS NULL OR valid_to > :timestamp)) " +
            "   OR valid_from >= :timestamp" +
            ")")
    Optional<SensorRegistrationId> findSensorRegistrationValidAtOrAfter(
            @BindBean("instrumentationId") InstrumentationId instrumentationId,
            @BindBean("sensorId") SensorId sensorId,
            @Bind("timestamp") Instant timestamp
    );

    @SqlUpdate("INSERT INTO sensor_registration(id, instrumentation_id, sensor_id, valid_from) " +
            "VALUES (:id.id, :instrumentationId.id, :sensorId.id, :validFrom)")
    void registerSensor_internal(
            @BindBean("id") SensorRegistrationId id,
            @BindBean("instrumentationId") InstrumentationId instrumentationId,
            @BindBean("sensorId") SensorId sensorId,
            @Bind("validFrom") Instant validFrom
    );

    default void unregisterSensor(InstrumentationId instrumentationId, SensorId sensorId, LocalDateTime validToUtc) {
        unregisterSensor(instrumentationId, sensorId, validToUtc.toInstant(UTC));
    }

    @SqlUpdate("UPDATE sensor_registration " +
            "SET valid_to = :validTo " +
            "WHERE instrumentation_id = :instrumentationId.id " +
            "AND sensor_id = :sensorId.id " +
            "AND valid_to IS NULL")
    void unregisterSensor(@BindBean("instrumentationId") InstrumentationId instrumentationId, @BindBean("sensorId") SensorId sensorId, @Bind("validTo") Instant validTo);

    @Transaction
    default boolean addMeasurement(String source, Instant createdAt, String type, double value, String unit) {
        Optional<Sensor> optSensor = findSensor(source);
        if (!optSensor.isPresent()) {
            LOGGER.debug("Ignoring measurement for unknown sensor [{}]", source);
            return false;
        }
        Sensor sensor = optSensor.get();

        Optional<InstrumentationId> optInstrumentationId = findInstrumentationId(sensor.getId(), createdAt);
        if (!optInstrumentationId.isPresent()) {
            LOGGER.debug("Ignoring measurement, instrumentation not found for sensor [{}] and timestamp [{}]", sensor, createdAt);
            return false;
        }
        InstrumentationId instrumentationId = optInstrumentationId.get();

        insertMeasurement(
                instrumentationId,
                sensor.getId(),
                new Timestamp(createdAt.toEpochMilli()),
                type,
                value,
                unit
        );

        return true;
    }

    @SqlQuery("SELECT * FROM sensor WHERE name = :name")
    Optional<Sensor> findSensor(@Bind("name") String name);

    @SqlQuery("SELECT i.id " +
            "FROM instrumentation i " +
            "INNER JOIN sensor_registration sr ON sr.instrumentation_id = i.id " +
            "WHERE " +
            "sr.sensor_id = :sensorId.id " +
            "AND sr.valid_from <= :measurementTimestamp " +
            "AND (sr.valid_to IS NULL OR sr.valid_to > :measurementTimestamp)")
    Optional<InstrumentationId> findInstrumentationId(@BindBean("sensorId") SensorId sensorId, @Bind("measurementTimestamp") Instant measurementTimestamp);

    @SqlUpdate("INSERT INTO measurement(instrumentation_id, sensor_id, timestamp, type, value, unit) VALUES (:instrumentationId.id, :sensorId.id, :timestamp, :type, :value, :unit)")
    void insertMeasurement(
            @BindBean("instrumentationId") InstrumentationId instrumentationId,
            @BindBean("sensorId") SensorId sensorId,
            @Bind("timestamp") Timestamp timestamp,
            @Bind("type") String type,
            @Bind("value") double value,
            @Bind("unit") String unit
    );

    @Transaction
    default QueryResult<Measurement> getMeasurements(InstrumentationId instrumentationId, SensorId sensorId, PaginationSettings paginationSettings) {
        int totalCount = getTotalMeasurementCount(instrumentationId, sensorId);
        PaginationInformation paginationInformation = PaginationInformation.calculate(totalCount, paginationSettings);

        List<Measurement> measurements = selectMeasurements(instrumentationId, sensorId, paginationInformation.getPerPage(), paginationInformation.getOffset());

        return new QueryResult<>(measurements, paginationInformation);
    }

    @SqlQuery("SELECT count(1) FROM measurement WHERE instrumentation_id = :instrumentationId.id AND sensor_id = :sensorId.id")
    int getTotalMeasurementCount(@BindBean("instrumentationId") InstrumentationId instrumentationId, @BindBean("sensorId") SensorId sensorId);

    @SqlQuery("SELECT * " +
            "FROM measurement " +
            "WHERE instrumentation_id = :instrumentationId.id " +
            "AND sensor_id = :sensorId.id " +
            "ORDER BY timestamp DESC " +
            "LIMIT :perPage " +
            "OFFSET :offset")
    List<Measurement> selectMeasurements(
            @BindBean("instrumentationId") InstrumentationId instrumentationId,
            @BindBean("sensorId") SensorId sensorId,
            @Bind("perPage") int perPage,
            @Bind("offset") int offset
    );
}
