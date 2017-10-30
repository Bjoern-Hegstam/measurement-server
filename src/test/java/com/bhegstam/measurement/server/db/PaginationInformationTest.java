package com.bhegstam.measurement.server.db;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;

public class PaginationInformationTest {
    private static final int PER_PAGE = 40;

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void calculate_lessThanOnePageOfItems() {
        testCalculation(
                PER_PAGE - 1,
                new PaginationSettings(PER_PAGE, 1),
                new PaginationInformation(1, PER_PAGE, 1, null, null)
        );
    }

    @Test
    public void calculate_exactlyOnePage() {
        testCalculation(
                PER_PAGE,
                new PaginationSettings(PER_PAGE, 1),
                new PaginationInformation(1, PER_PAGE, 1, null, null)
        );
    }

    @Test
    public void calculate_firstPage_hasNextPage() {
        testCalculation(
                PER_PAGE + 1,
                new PaginationSettings(PER_PAGE, 1),
                new PaginationInformation(2, PER_PAGE, 1, 2, null)
        );
    }

    @Test
    public void calculate_secondPage_oneLessThanThirdPage() {
        testCalculation(
                2*PER_PAGE - 1,
                new PaginationSettings(PER_PAGE, 2),
                new PaginationInformation(2, PER_PAGE, 2, null, 1)
        );
    }

    @Test
    public void calculate_secondPage_exactly() {
        testCalculation(
                2*PER_PAGE,
                new PaginationSettings(PER_PAGE, 2),
                new PaginationInformation(2, PER_PAGE, 2, null, 1)
        );
    }

    @Test
    public void calculate_secondPage_hasNextPage() {
        testCalculation(
                2*PER_PAGE + 1,
                new PaginationSettings(PER_PAGE, 2),
                new PaginationInformation(3, PER_PAGE, 2, 3, 1)
        );
    }

    @Test
    public void calculate_zeroItems() {
        testCalculation(
                0,
                new PaginationSettings(PER_PAGE, 1),
                new PaginationInformation(1, PER_PAGE, 1, null, null)
        );
    }

    @Test
    public void calculate_zeroItemsPerPage() {
        // then
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("PerPage must be greater than zero, got <0>");

        // when
        PaginationInformation.calculate(PER_PAGE, new PaginationSettings(0, 1));
    }

    private void testCalculation(int itemCount, PaginationSettings settings, PaginationInformation expectedInformation) {
        // when
        PaginationInformation calculated = PaginationInformation.calculate(itemCount, settings);

        // then
        errorCollector.checkThat("Page count", calculated.getPageCount(), is(expectedInformation.getPageCount()));
        errorCollector.checkThat("Per page", calculated.getPerPage(), is(expectedInformation.getPerPage()));
        errorCollector.checkThat("Page", calculated.getPage(), is(expectedInformation.getPage()));
        errorCollector.checkThat("Prev page", calculated.getPrevPage(), is(expectedInformation.getPrevPage()));
        errorCollector.checkThat("Next page", calculated.getNextPage(), is(expectedInformation.getNextPage()));
    }
}