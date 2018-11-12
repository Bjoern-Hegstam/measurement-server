package com.bhegstam.measurement.port.rest.measurement;

import com.bhegstam.measurement.configuration.MeasurementServerApplicationConfiguration;
import com.bhegstam.measurement.db.PaginationInformation;
import com.bhegstam.measurement.domain.*;
import com.bhegstam.measurement.port.rest.JsonMapper;
import com.bhegstam.measurement.util.DropwizardAppRuleFactory;
import com.bhegstam.measurement.util.TestDatabaseSetup;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.ExternalResourceSupport;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.LocalDateTime;

import static com.bhegstam.measurement.port.rest.ResponseTestUtil.assertResponseStatus;
import static java.time.ZoneOffset.UTC;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

@ExtendWith(ExternalResourceSupport.class)
@ExtendWith(TestDatabaseSetup.class)
public class MeasurementIntegrationTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String TYPE = "TYPE";
    private static final String UNIT = "UNIT";
    private static final InstrumentationId INSTRUMENTATION_ID_1 = InstrumentationId.parse("instrumentation-id-1");
    private static final InstrumentationId INSTRUMENTATION_ID_2 = InstrumentationId.parse("instrumentation-id-2");
    private static final Sensor SENSOR_1 = new Sensor(new SensorId(), "sensor-1");
    private static final Sensor SENSOR_2 = new Sensor(new SensorId(), "sensor-2");
    private static final Sensor SENSOR_3 = new Sensor(new SensorId(), "sensor-3");

    @Rule
    public static final DropwizardAppRule<MeasurementServerApplicationConfiguration> service = DropwizardAppRuleFactory.forIntegrationTest();

    @Inject
    private MeasurementRepository measurementRepository;
    private MeasurementApi api;
    private final JsonMapper jsonMapper = new JsonMapper();

    @BeforeEach
    void setUp() {
        String serviceUrl = "http://localhost:" + service.getLocalPort() + "/api/";
        api = new MeasurementApi(serviceUrl);

        measurementRepository.addSensor(SENSOR_1);
        measurementRepository.addSensor(SENSOR_2);
        measurementRepository.addSensor(SENSOR_3);
    }

    @Test
    void getInstrumentations_emptyDb() {
        // when
        Response response = api.getInstrumentations();

        // then
        assertResponseStatus(response, OK);

        JsonNode responseJson = jsonMapper.read(response);
        assertThat(responseJson.size(), is(0));
    }

    @Test
    void getInstrumentations() {
        // given
        measurementRepository.addInstrumentation(INSTRUMENTATION_ID_1, "instrumentation-1");
        LocalDateTime baseSensorValidFrom = LocalDateTime.of(2010, 3, 14, 12, 0);
        measurementRepository.registerSensor(INSTRUMENTATION_ID_1, SENSOR_1.getId(), baseSensorValidFrom);
        measurementRepository.unregisterSensor(INSTRUMENTATION_ID_1, SENSOR_1.getId(), baseSensorValidFrom.plusDays(1));
        measurementRepository.registerSensor(INSTRUMENTATION_ID_1, SENSOR_2.getId(), baseSensorValidFrom);

        measurementRepository.addInstrumentation(INSTRUMENTATION_ID_2, "instrumentation-2");
        measurementRepository.registerSensor(INSTRUMENTATION_ID_2, SENSOR_3.getId(), baseSensorValidFrom);

        // when
        Response response = api.getInstrumentations();

        // then
        assertResponseStatus(response, OK);

        JsonNode responseJson = jsonMapper.read(response);
        JsonNode instrumentationJson1 = responseJson.get(0);
        assertThat(instrumentationJson1.get("name").asText(), is("instrumentation-1"));
        JsonNode registrationsJson1 = instrumentationJson1.get("sensorRegistrations");
        assertThat(registrationsJson1.size(), is(2));
        assertThat(registrationsJson1.get(0).get("sensor").get("id").asText(), is(SENSOR_2.getId().getId()));
        assertThat(registrationsJson1.get(0).get("validFromUtc").asText(), is(baseSensorValidFrom.toString()));
        assertThat(registrationsJson1.get(0).get("validToUtc"), is(nullValue()));
        assertThat(registrationsJson1.get(1).get("sensor").get("id").asText(), is(SENSOR_1.getId().getId()));
        assertThat(registrationsJson1.get(1).get("validFromUtc").asText(), is(baseSensorValidFrom.toString()));
        assertThat(registrationsJson1.get(1).get("validToUtc").asText(), is(baseSensorValidFrom.plusDays(1).toString()));

        JsonNode instrumentationJson2 = responseJson.get(1);
        assertThat(instrumentationJson2.get("name").asText(), is("instrumentation-2"));
    }

    @Test
    void getMeasurements_emptyDb() {
        // when
        Response response = api.getMeasurements(INSTRUMENTATION_ID_1, SensorId.parse("id"), 40, 1);

        // then
        assertResponseStatus(response, OK);

        PaginationInformation paginationInformation = new PaginationInformation(0, 1, 40, 1, null, null);
        assertPaginationHeaders(response, paginationInformation);

        JsonNode responseJson = jsonMapper.read(response);
        assertThat(responseJson.size(), is(0));
    }

    @Test
    void getMeasurements() {
        // given
        measurementRepository.addInstrumentation(INSTRUMENTATION_ID_1, "instrumentation-1");
        measurementRepository.registerSensor(INSTRUMENTATION_ID_1, SENSOR_1.getId(), LocalDateTime.now(UTC));
        measurementRepository.registerSensor(INSTRUMENTATION_ID_1, SENSOR_2.getId(), LocalDateTime.now(UTC));

        insertMeasurements(SENSOR_1.getName(), 100);
        insertMeasurements(SENSOR_2.getName(), 100);

        // when
        Response response = api.getMeasurements(INSTRUMENTATION_ID_1, SENSOR_1.getId(), 40, 1);

        // then
        assertResponseStatus(response, OK);

        assertPaginationHeaders(response, new PaginationInformation(100, 3, 40, 1, 2, null));

        JsonNode responseJson = jsonMapper.read(response);
        assertThat(responseJson.size(), is(40));

        JsonNode firstMeasurement = responseJson.get(0);
        JsonNode lastMeasurement = responseJson.get(responseJson.size() - 1);

        long firstCreatedAt = firstMeasurement.get("createdAtMillis").asLong();
        long lastCreatedAt = lastMeasurement.get("createdAtMillis").asLong();

        assertTrue("Measurements by createdAt desc", firstCreatedAt > lastCreatedAt);
    }

    @Test
    void postMeasurement() {
        // given
        ObjectNode requestBody = OBJECT_MAPPER
                .createObjectNode()
                .put("source", SENSOR_1.getId().getId())
                .put("type", TYPE)
                .put("value", 17)
                .put("unit", UNIT);

        // when
        Response response = api.postMeasurement(requestBody);

        // then
        assertResponseStatus(response, CREATED);
    }

    private void insertMeasurements(String source, int count) {
        Instant baseCreatedAt = Instant.now();
        for (int i = 0; i < count; i++) {
            measurementRepository.addMeasurement(source, baseCreatedAt.plusSeconds(i), TYPE, i, UNIT);
        }
    }

    private void assertPaginationHeaders(Response response, PaginationInformation paginationInformation) {
        checkHeader(response, PaginationHeader.TOTAL,  paginationInformation.getTotalItemCount());
        checkHeader(response, PaginationHeader.TOTAL_PAGE, paginationInformation.getPageCount());
        checkHeader(response, PaginationHeader.PAGE, paginationInformation.getPage());
        checkHeader(response, PaginationHeader.NEXT_PAGE, paginationInformation.getNextPage().orElse(null));
        checkHeader(response, PaginationHeader.PREV_PAGE, paginationInformation.getPrevPage().orElse(null));
        checkHeader(response, PaginationHeader.PER_PAGE, paginationInformation.getPerPage());
    }

    private void checkHeader(Response response, String header, Integer expectedValue) {
        if (expectedValue != null) {
            assertThat(header, response.getHeaderString(header), is(Integer.toString(expectedValue)));
        } else {
            assertNull(header, response.getHeaderString(header));
        }
    }
}
