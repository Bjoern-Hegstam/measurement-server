package com.bhegstam.measurement.server.web;

import com.bhegstam.measurement.server.db.PaginationInformation;
import com.bhegstam.webutil.webapp.ResultBuilder;

public class PaginationHeader {
    public static final String TOTAL = "X-Total";
    public static final String TOTAL_PAGE = "X-Total-Page";
    public static final String PAGE = "X-Page";
    public static final String PER_PAGE = "X-Per-Page";
    public static final String NEXT_PAGE = "X-Next-Page";
    public static final String PREV_PAGE = "X-Prev-Page";

    public static void appendHeaders(ResultBuilder resultBuilder, PaginationInformation paginationInformation) {
        resultBuilder
                .header(PaginationHeader.TOTAL, Integer.toString(paginationInformation.getTotalItemCount()))
                .header(PaginationHeader.TOTAL_PAGE, Integer.toString(paginationInformation.getPageCount()))
                .header(PaginationHeader.PAGE, Integer.toString(paginationInformation.getPage()))
                .header(PaginationHeader.PER_PAGE, Integer.toString(paginationInformation.getPerPage()));

        paginationInformation.getPrevPage().map(pp -> resultBuilder.header(PaginationHeader.PREV_PAGE, Integer.toString(pp)));
        paginationInformation.getNextPage().map(np -> resultBuilder.header(PaginationHeader.NEXT_PAGE, Integer.toString(np)));
    }
}
