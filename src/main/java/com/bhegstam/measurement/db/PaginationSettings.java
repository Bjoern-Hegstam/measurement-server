package com.bhegstam.measurement.db;

public class PaginationSettings {
    public static final int DEFAULT_ITEMS_PER_PAGE = 40;
    public static final int DEFAULT_PAGE = 1;

    private final int itemsPerPage;
    private final int page;

    public PaginationSettings(int itemsPerPage, int page) {
        this.itemsPerPage = itemsPerPage;
        this.page = page;
    }

    int itemsPerPage() {
        return itemsPerPage;
    }

    int page() {
        return page;
    }
}
