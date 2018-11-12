package com.bhegstam.measurement.port.persistence;

import com.bhegstam.measurement.db.PaginationInformation;
import com.bhegstam.measurement.db.PaginationSettings;
import com.bhegstam.measurement.db.QueryResult;
import com.bhegstam.measurement.domain.*;
import com.bhegstam.measurement.util.TestDatabaseSetup;
import org.jdbi.v3.core.Jdbi;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.bhegstam.measurement.util.Matchers.isNotPresent;
import static com.bhegstam.measurement.util.Matchers.isPresentAnd;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(TestDatabaseSetup.class)
public class JdbiMeasurementRepositoryTest {
    private static final InstrumentationId INSTRUMENTATION_ID = InstrumentationId.parse("instrumentation-id");
    private static final Sensor SENSOR_1 = new Sensor(new SensorId(), "sensor-1");
    private static final Sensor SENSOR_2 = new Sensor(new SensorId(), "sensor-2");
    private static final String TYPE = "TYPE";
    private static final String UNIT = "UNIT";
    private static final LocalDateTime VALID_FROM = LocalDateTime.now(UTC);

    @Inject
    private Jdbi jdbi;
    private MeasurementRepository measurementRepository;

    @Rule
    public TestDatabaseSetup testDatabaseSetup = new TestDatabaseSetup();

    @BeforeEach
    void setUp() {
        measurementRepository = jdbi.onDemand(JdbiMeasurementRepository.class);
    }

    @Test
    void getInstrumentations_emptyDb() {
        // when
        List<Instrumentation> instrumentations = measurementRepository.getInstrumentations();

        // then
        assertTrue(instrumentations.isEmpty());
    }

    @Test
    void addInstrumentation() {
        // when
        measurementRepository.addInstrumentation(INSTRUMENTATION_ID, "name");

        // then
        Instrumentation instrumentation = measurementRepository.getInstrumentations().get(0);
        assertThat(instrumentation.getId(), is(INSTRUMENTATION_ID));
        assertThat(instrumentation.getName(), is("name"));
        assertTrue(instrumentation.getRegistrations().isEmpty());
    }

    @Test
    void registerSensor() {
        // given
        measurementRepository.addInstrumentation(INSTRUMENTATION_ID, "name");
        measurementRepository.addSensor(SENSOR_1);
        LocalDateTime validFrom = LocalDateTime.now(UTC);

        // when
        measurementRepository.registerSensor(INSTRUMENTATION_ID, SENSOR_1.getId(), validFrom);

        // then
        Instrumentation instrumentation = measurementRepository.getInstrumentations().get(0);
        assertThat(instrumentation.getRegistrations().size(), is(1));
    }

    @Test
    void unregisterSensor() {
        // given
        measurementRepository.addInstrumentation(INSTRUMENTATION_ID, "name");
        measurementRepository.addSensor(SENSOR_1);
        measurementRepository.registerSensor(INSTRUMENTATION_ID, SENSOR_1.getId(), VALID_FROM);

        // when
        measurementRepository.unregisterSensor(INSTRUMENTATION_ID, SENSOR_1.getId(), VALID_FROM.plusSeconds(10));

        // then
        Instrumentation instrumentation = measurementRepository.getInstrumentations().get(0);
        assertThat(instrumentation.getRegistrations().get(0).getValidToUtc(), is(VALID_FROM.plusSeconds(10).truncatedTo(ChronoUnit.MILLIS)));
    }

    @Test
    void sensorRegistrationsForSameSensorNotAllowedToOverlap() {
        // given
        measurementRepository.addInstrumentation(INSTRUMENTATION_ID, "name");
        measurementRepository.addSensor(SENSOR_1);
        measurementRepository.registerSensor(INSTRUMENTATION_ID, SENSOR_1.getId(), VALID_FROM);

        try {
            measurementRepository.registerSensor(INSTRUMENTATION_ID, SENSOR_1.getId(), VALID_FROM);
            fail("registration active, register new with same validFrom");
        } catch (SensorRegistrationNotAllowedException e) {
            // good
        }

        try {
            measurementRepository.registerSensor(INSTRUMENTATION_ID, SENSOR_1.getId(), VALID_FROM.minusSeconds(1));
            fail("registration active, register new with earlier validFrom");
        } catch (SensorRegistrationNotAllowedException e) {
            // good
        }
    }

    @Test
    void registerMultipleSensorsForSamePeriod() {
        // given
        measurementRepository.addInstrumentation(INSTRUMENTATION_ID, "name");
        measurementRepository.addSensor(SENSOR_1);
        measurementRepository.addSensor(SENSOR_2);

        // when
        measurementRepository.registerSensor(INSTRUMENTATION_ID, SENSOR_1.getId(), VALID_FROM);
        measurementRepository.registerSensor(INSTRUMENTATION_ID, SENSOR_2.getId(), VALID_FROM);

        // then
        Instrumentation instrumentation = measurementRepository.getInstrumentations().get(0);
        assertThat(instrumentation.getRegistrations().size(), is(2));
    }

    @Test
    void multipleRegistrationsForSameSensor() {
        // given
        measurementRepository.addInstrumentation(INSTRUMENTATION_ID, "name");
        measurementRepository.addSensor(SENSOR_1);

        measurementRepository.registerSensor(INSTRUMENTATION_ID, SENSOR_1.getId(), VALID_FROM);
        measurementRepository.unregisterSensor(INSTRUMENTATION_ID, SENSOR_1.getId(), VALID_FROM.plusSeconds(5));

        // when
        measurementRepository.registerSensor(INSTRUMENTATION_ID, SENSOR_1.getId(), VALID_FROM.plusSeconds(5));

        // then
        Instrumentation instrumentation = measurementRepository.getInstrumentations().get(0);
        assertThat(instrumentation.getRegistrations().size(), is(2));
    }

    @Test
    void getMeasurements_emptyDb() {
        // given
        measurementRepository.addInstrumentation(INSTRUMENTATION_ID, "name");
        measurementRepository.addSensor(SENSOR_1);

        // when
        QueryResult<Measurement> queryResult = measurementRepository.getMeasurements(INSTRUMENTATION_ID, SENSOR_1.getId(), new PaginationSettings(1, 1));

        // then
        assertAll(
                () -> assertThat("Data count", queryResult.getData().size(), is(0)),
                () -> {
                    PaginationInformation paginationInformation = queryResult
                            .getPaginationInformation()
                            .get();
                    assertAll(
                            () -> assertThat("Total item count", paginationInformation.getTotalItemCount(), is(0)),
                            () -> assertThat("Page count", paginationInformation.getPageCount(), is(1)),
                            () -> assertThat("Per page", paginationInformation.getPerPage(), is(1)),
                            () -> assertThat("Prev page", paginationInformation.getPrevPage(), isNotPresent()),
                            () -> assertThat("Next page", paginationInformation.getNextPage(), isNotPresent()),
                            () -> assertThat("Offset", paginationInformation.getOffset(), is(0))
                    );
                }
        );
    }

    @Test
    void addAndGetMeasurement() {
        // given
        createInstrumentationWithRegisteredSensors();

        Instant createdAt = Instant.now();
        assertTrue(measurementRepository.addMeasurement(SENSOR_1.getName(), createdAt, TYPE, 17, UNIT));
        assertTrue(measurementRepository.addMeasurement(SENSOR_2.getName(), createdAt, TYPE, 18, UNIT));

        // when
        QueryResult<Measurement> queryResult = measurementRepository.getMeasurements(INSTRUMENTATION_ID, SENSOR_1.getId(), new PaginationSettings(1, 1));

        // then
        assertAll(
                () -> {
                    assertThat("Data count", queryResult.getData().size(), is(1));
                    Measurement measurement = queryResult.getData().get(0);
                    assertAll(
                            () -> assertThat("source", measurement.getSensorId(), is(SENSOR_1.getId())),
                            () -> assertThat("createdAt", measurement.getCreatedAt(), is(createdAt.truncatedTo(ChronoUnit.MILLIS))),
                            () -> assertThat("type", measurement.getType(), is(TYPE)),
                            () -> assertThat("value", measurement.getValue(), is(17.0)),
                            () -> assertThat("unit", measurement.getUnit(), is(UNIT))
                    );
                },
                () -> {
                    PaginationInformation paginationInformation = queryResult
                            .getPaginationInformation()
                            .get();
                    assertAll(
                            () -> assertThat("Total item count", paginationInformation.getTotalItemCount(), is(1)),
                            () -> assertThat("Page count", paginationInformation.getPageCount(), is(1)),
                            () -> assertThat("Per page", paginationInformation.getPerPage(), is(1)),
                            () -> assertThat("Prev page", paginationInformation.getPrevPage(), isNotPresent()),
                            () -> assertThat("Next page", paginationInformation.getNextPage(), isNotPresent()),
                            () -> assertThat("Offset", paginationInformation.getOffset(), is(0))
                    );
                }
        );
    }

    @Test
    void getMeasurements_firstPage_hasNextPage() {
        // given
        createInstrumentationWithRegisteredSensors();

        insertMeasurements(SENSOR_1.getName(), 40);
        insertMeasurements(SENSOR_2.getName(), 40);

        // when
        QueryResult<Measurement> queryResult = measurementRepository.getMeasurements(INSTRUMENTATION_ID, SENSOR_1.getId(), new PaginationSettings(30, 1));

        // then
        assertAll(
                () -> assertThat("Data count", queryResult.getData().size(), is(30)),
                () -> {
                    PaginationInformation paginationInformation = queryResult
                            .getPaginationInformation()
                            .get();
                    assertAll(
                            () -> assertThat("Total item count", paginationInformation.getTotalItemCount(), is(40)),
                            () -> assertThat("Page count", paginationInformation.getPageCount(), is(2)),
                            () -> assertThat("Per page", paginationInformation.getPerPage(), is(30)),
                            () -> assertThat("Prev page", paginationInformation.getPrevPage(), isNotPresent()),
                            () -> assertThat("Next page", paginationInformation.getNextPage(), isPresentAnd(is(2))),
                            () -> assertThat("Offset", paginationInformation.getOffset(), is(0))
                    );
                }
        );
    }

    @Test
    void getMeasurements_secondPage_doesNotHaveNextPage() {
        // given
        createInstrumentationWithRegisteredSensors();

        insertMeasurements(SENSOR_1.getName(), 40);
        insertMeasurements(SENSOR_2.getName(), 40);

        // when
        QueryResult<Measurement> queryResult = measurementRepository.getMeasurements(INSTRUMENTATION_ID, SENSOR_1.getId(), new PaginationSettings(30, 2));

        // then
        assertAll(
                () -> assertThat("Data count", queryResult.getData().size(), is(10)),
                () -> {
                    PaginationInformation paginationInformation = queryResult
                            .getPaginationInformation()
                            .get();
                    assertAll(
                            () -> assertThat("Total item count", paginationInformation.getTotalItemCount(), is(40)),
                            () -> assertThat("Page count", paginationInformation.getPageCount(), is(2)),
                            () -> assertThat("Per page", paginationInformation.getPerPage(), is(30)),
                            () -> assertThat("Prev page", paginationInformation.getPrevPage(), isPresentAnd(is(1))),
                            () -> assertThat("Next page", paginationInformation.getNextPage(), isNotPresent()),
                            () -> assertThat("Offset", paginationInformation.getOffset(), is(30))
                    );
                }
        );
    }

    private void createInstrumentationWithRegisteredSensors() {
        LocalDateTime createdAt = LocalDateTime.now(UTC);

        measurementRepository.addInstrumentation(INSTRUMENTATION_ID, "name");
        measurementRepository.addSensor(SENSOR_1);
        measurementRepository.addSensor(SENSOR_2);
        measurementRepository.registerSensor(INSTRUMENTATION_ID, SENSOR_1.getId(), createdAt);
        measurementRepository.registerSensor(INSTRUMENTATION_ID, SENSOR_2.getId(), createdAt);
    }

    private void insertMeasurements(String source, int count) {
        for (int i = 0; i < count; i++) {
            measurementRepository.addMeasurement(source, Instant.now(), TYPE, i, UNIT);
        }
    }
}