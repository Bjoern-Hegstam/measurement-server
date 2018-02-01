package com.bhegstam.measurement.server.measurement.api;

import com.bhegstam.measurement.server.db.PaginationInformation;
import com.bhegstam.measurement.server.db.QueryResult;
import com.bhegstam.measurement.server.measurement.db.MeasurementRepository;
import com.bhegstam.measurement.server.web.PaginationHeader;
import com.bhegstam.webutil.webapp.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.bhegstam.util.Mocks.mockRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MeasurementApiControllerTest {

    private MeasurementRepository repository;
    private MeasurementApiController controller;

    @BeforeEach
    void setUp() {
        repository = mock(MeasurementRepository.class);
        controller = new MeasurementApiController(repository);
    }

    @Test
    void getMeasurements_pagination_headers() {
        // given
        when(repository.find(any(), any())).thenReturn(
                new QueryResult<>(
                        new ArrayList<>(),
                        new PaginationInformation(200,5, 40, 1, 2, null)
                )
        );

        // when
        Result result = controller.getMeasurementsForSource(mockRequest());

        // then
        checkHeader(result, PaginationHeader.TOTAL, "200");
        checkHeader(result, PaginationHeader.TOTAL_PAGE, "5");
        checkHeader(result, PaginationHeader.PAGE, "1");
        checkHeader(result, PaginationHeader.NEXT_PAGE, "2");
        checkHeader(result, PaginationHeader.PREV_PAGE, null);
        checkHeader(result, PaginationHeader.PER_PAGE, "40");
    }

    private void checkHeader(Result result, String header, String expectedValue) {
        assertThat(header, result.getHeaders().get(header), is(expectedValue));
    }
}