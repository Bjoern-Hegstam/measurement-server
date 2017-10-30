package com.bhegstam.measurement.server.db;

import com.bhegstam.webutil.webapp.Request;

import java.util.Optional;

public class PaginationSettings {
    public static final int DEFAULT_ITEMS_PER_PAGE = 40;
    public static final int DEFAULT_PAGE = 1;

    private final int itemsPerPage;
    private final int page;

    public PaginationSettings(int itemsPerPage, int page) {
        this.itemsPerPage = itemsPerPage;
        this.page = page;
    }

    public static PaginationSettings fromQuery(Request request) {
        return new PaginationSettings(
                Optional.ofNullable(request.queryParams("per_page")).map(Integer::parseInt).orElse(DEFAULT_ITEMS_PER_PAGE),
                Optional.ofNullable(request.queryParams("page")).map(Integer::parseInt).orElse(DEFAULT_PAGE)
        );
    }

    public int itemsPerPage() {
        return itemsPerPage;
    }

    public int page() {
        return page;
    }
}
