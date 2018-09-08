package com.bhegstam.measurement.port.rest.measurement;

import com.bhegstam.measurement.db.PaginationInformation;

import javax.ws.rs.core.Response;

class PaginationHeader {
    static final String TOTAL = "X-Total";
    static final String TOTAL_PAGE = "X-Total-Page";
    static final String PAGE = "X-Page";
    static final String PER_PAGE = "X-Per-Page";
    static final String NEXT_PAGE = "X-Next-Page";
    static final String PREV_PAGE = "X-Prev-Page";

    static void appendHeaders(Response.ResponseBuilder responseBuilder, PaginationInformation paginationInformation) {
        responseBuilder
                .header(PaginationHeader.TOTAL, Integer.toString(paginationInformation.getTotalItemCount()))
                .header(PaginationHeader.TOTAL_PAGE, Integer.toString(paginationInformation.getPageCount()))
                .header(PaginationHeader.PAGE, Integer.toString(paginationInformation.getPage()))
                .header(PaginationHeader.PER_PAGE, Integer.toString(paginationInformation.getPerPage()));

        paginationInformation.getPrevPage().map(pp -> responseBuilder.header(PaginationHeader.PREV_PAGE, Integer.toString(pp)));
        paginationInformation.getNextPage().map(np -> responseBuilder.header(PaginationHeader.NEXT_PAGE, Integer.toString(np)));
    }
}
