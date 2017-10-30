package com.bhegstam.measurement.server.measurement.api;

import com.bhegstam.measurement.server.db.PaginationInformation;
import com.bhegstam.measurement.server.db.QueryResult;
import com.bhegstam.measurement.server.measurement.db.MeasurementRepository;
import com.bhegstam.measurement.server.web.PaginationHeader;
import com.bhegstam.webutil.webapp.Result;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.ArrayList;

import static com.bhegstam.util.Mocks.mockRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MeasurementApiControllerTest {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    private MeasurementRepository repository;
    private MeasurementApiController controller;

    @Before
    public void setUp() throws Exception {
        repository = mock(MeasurementRepository.class);
        controller = new MeasurementApiController(repository);
    }

    @Test
    public void getMeasurements_pagination_headers() {
        // given
        when(repository.find(any())).thenReturn(
                new QueryResult<>(
                        new ArrayList<>(),
                        new PaginationInformation(200,5, 40, 1, 2, null)
                )
        );

        // when
        Result result = controller.getMeasurements(mockRequest());

        // then
        checkHeader(result, PaginationHeader.TOTAL, "200");
        checkHeader(result, PaginationHeader.TOTAL_PAGE, "5");
        checkHeader(result, PaginationHeader.PAGE, "1");
        checkHeader(result, PaginationHeader.NEXT_PAGE, "2");
        checkHeader(result, PaginationHeader.PREV_PAGE, null);
        checkHeader(result, PaginationHeader.PER_PAGE, "40");
    }

    private void checkHeader(Result result, String header, String expectedValue) {
        errorCollector.checkThat(header, result.getHeaders().get(header), is(expectedValue));
    }
}