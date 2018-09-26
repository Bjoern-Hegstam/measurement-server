package com.bhegstam.measurement.port.rest.measurement;

import com.bhegstam.measurement.configuration.MeasurementServerApplicationConfiguration;
import com.bhegstam.measurement.db.PaginationInformation;
import com.bhegstam.measurement.domain.MeasurementRepository;
import com.bhegstam.measurement.port.rest.JsonMapper;
import com.bhegstam.measurement.util.DropwizardAppRuleFactory;
import com.bhegstam.measurement.util.TestDatabaseSetup;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

import static com.bhegstam.measurement.port.rest.ResponseTestUtil.assertResponseStatus;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@ExtendWith(ExternalResourceSupport.class)
@ExtendWith(TestDatabaseSetup.class)
public class MeasurementIntegrationTest {
    private static final String TYPE = "TYPE";
    private static final String UNIT = "UNIT";
    private static final String SOURCE_1_ID = "s1";
    private static final String SOURCE_2_ID = "s2";

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
    }

    @Test
    void getSources_emptyDb() {
        // when
        Response response = api.getSources();

        // then
        assertResponseStatus(response, OK);

        JsonNode responseJson = jsonMapper.read(response);
        assertThat(responseJson.size(), is(0));
    }

    @Test
    void getSources() {
        // given
        measurementRepository.addMeasurement(SOURCE_1_ID, Instant.now(), TYPE, 1.0, UNIT);
        measurementRepository.addMeasurement(SOURCE_2_ID, Instant.now(), TYPE, 1.0, UNIT);

        // when
        Response response = api.getSources();

        // then
        assertResponseStatus(response, OK);

        JsonNode responseJson = jsonMapper.read(response);
        assertThat(responseJson.size(), is(2));

        assertThat(responseJson.get(0).get("name").asText(), is(SOURCE_1_ID));
        assertThat(responseJson.get(1).get("name").asText(), is(SOURCE_2_ID));
    }

    @Test
    void getMeasurements_emptyDb() {
        // when
        Response response = api.getMeasurements("id", 40, 1);

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
        insertMeasurements(SOURCE_1_ID, 100);
        insertMeasurements(SOURCE_2_ID, 100);

        // when
        Response response = api.getMeasurements(SOURCE_1_ID, 40, 1);

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

    private void insertMeasurements(String sourceId, int count) {
        Instant baseCreatedAt = Instant.now();
        for (int i = 0; i < count; i++) {
            measurementRepository.addMeasurement(sourceId, baseCreatedAt.plusSeconds(i), TYPE, i, UNIT);
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
