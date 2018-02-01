package com.bhegstam.measurement.server.db;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaginationInformationTest {
    private static final int PER_PAGE = 40;

    @Test
    void calculate_lessThanOnePageOfItems() {
        testCalculation(
                new PaginationSettings(PER_PAGE, 1),
                new PaginationInformation(PER_PAGE - 1, 1, PER_PAGE, 1, null, null)
        );
    }

    @Test
    void calculate_exactlyOnePage() {
        testCalculation(
                new PaginationSettings(PER_PAGE, 1),
                new PaginationInformation(PER_PAGE, 1, PER_PAGE, 1, null, null)
        );
    }

    @Test
    void calculate_firstPage_hasNextPage() {
        testCalculation(
                new PaginationSettings(PER_PAGE, 1),
                new PaginationInformation(PER_PAGE + 1, 2, PER_PAGE, 1, 2, null)
        );
    }

    @Test
    void calculate_secondPage_oneLessThanThirdPage() {
        testCalculation(
                new PaginationSettings(PER_PAGE, 2),
                new PaginationInformation(2*PER_PAGE - 1, 2, PER_PAGE, 2, null, 1)
        );
    }

    @Test
    void calculate_secondPage_exactly() {
        testCalculation(
                new PaginationSettings(PER_PAGE, 2),
                new PaginationInformation(2*PER_PAGE, 2, PER_PAGE, 2, null, 1)
        );
    }

    @Test
    void calculate_secondPage_hasNextPage() {
        testCalculation(
                new PaginationSettings(PER_PAGE, 2),
                new PaginationInformation(2*PER_PAGE + 1, 3, PER_PAGE, 2, 3, 1)
        );
    }

    @Test
    void calculate_zeroItems() {
        testCalculation(
                new PaginationSettings(PER_PAGE, 1),
                new PaginationInformation(0, 1, PER_PAGE, 1, null, null)
        );
    }

    @Test
    void calculate_zeroItemsPerPage() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PaginationInformation.calculate(PER_PAGE, new PaginationSettings(0, 1))
        );

        assertThat(exception.getMessage(), is("PerPage must be greater than zero, got <0>"));
    }

    private void testCalculation(PaginationSettings settings, PaginationInformation expectedInformation) {
        // when
        PaginationInformation calculated = PaginationInformation.calculate(expectedInformation.getTotalItemCount(), settings);

        // then
        assertAll(
                () -> assertThat("Page count", calculated.getPageCount(), is(expectedInformation.getPageCount())),
                () -> assertThat("Per page", calculated.getPerPage(), is(expectedInformation.getPerPage())),
                () -> assertThat("Page", calculated.getPage(), is(expectedInformation.getPage())),
                () -> assertThat("Prev page", calculated.getPrevPage(), is(expectedInformation.getPrevPage())),
                () -> assertThat("Next page", calculated.getNextPage(), is(expectedInformation.getNextPage()))
        );
    }

    @Test
    void offset() {
        testOffset("First page", PER_PAGE, 1, 0);
        testOffset("Second page", PER_PAGE, 2, PER_PAGE);
    }

    private void testOffset(String reason, int perPage, int page, int expectedOffset) {
        // given
        PaginationInformation paginationInformation = new PaginationInformation(10*perPage, 10, perPage, page, null, null);

        // then
        assertThat(reason, paginationInformation.getOffset(), is(expectedOffset));
    }
}