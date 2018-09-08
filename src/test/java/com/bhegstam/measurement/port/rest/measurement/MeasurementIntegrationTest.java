package com.bhegstam.measurement.port.rest.measurement;

import com.bhegstam.measurement.configuration.MeasurementServerApplicationConfiguration;
import com.bhegstam.measurement.db.PaginationInformation;
import com.bhegstam.measurement.domain.MeasurementRepository;
import com.bhegstam.measurement.port.persistence.RepositoryFactory;
import com.bhegstam.measurement.port.rest.JsonMapper;
import com.bhegstam.measurement.util.DropwizardAppRuleFactory;
import com.fasterxml.jackson.databind.JsonNode;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static com.bhegstam.measurement.port.rest.ResponseTestUtil.assertResponseStatus;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class MeasurementIntegrationTest {
    @ClassRule
    public static final DropwizardAppRule<MeasurementServerApplicationConfiguration> service = DropwizardAppRuleFactory.forIntegrationTest();

    private MeasurementRepository measurementRepository;
    private MeasurementApi api;
    private final JsonMapper jsonMapper = new JsonMapper();

    @Before
    public void setUp() {
        String serviceUrl = "http://localhost:" + service.getLocalPort() + "/api/";
        RepositoryFactory repositoryFactory = new RepositoryFactory(service.getEnvironment(), service.getConfiguration().getDataSourceFactory());
        measurementRepository = repositoryFactory.createMeasurementRepository();
        api = new MeasurementApi(serviceUrl);
    }

    @Test
    public void getSources_emptyDb() {
        // when
        Response response = api.getSources();

        // then
        assertResponseStatus(response, OK);

        JsonNode responseJson = jsonMapper.read(response);
        assertThat(responseJson.size(), is(0));
    }

    @Test
    public void getMeasurements_emptyDb() {
        // when
        Response response = api.getMeasurements("id", 40, 1);

        // then
        assertResponseStatus(response, OK);

        PaginationInformation paginationInformation = new PaginationInformation(0, 1, 40, 1, null, null);
        assertPaginationHeaders(response, paginationInformation);

        JsonNode responseJson = jsonMapper.read(response);
        assertThat(responseJson.size(), is(0));
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
