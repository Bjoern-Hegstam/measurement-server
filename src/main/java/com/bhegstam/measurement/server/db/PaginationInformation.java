package com.bhegstam.measurement.server.db;

import java.util.Optional;

public class PaginationInformation {
    private final int pageCount;
    private final int perPage;
    private final int page;
    private final Integer nextPage;
    private final Integer prevPage;

    public PaginationInformation(int pageCount, int perPage, int page, Integer nextPage, Integer prevPage) {
        this.pageCount = pageCount;
        this.perPage = perPage;
        this.page = page;
        this.nextPage = nextPage;
        this.prevPage = prevPage;
    }

    public static PaginationInformation calculate(int itemCount, PaginationSettings settings) {
        if (settings.itemsPerPage() <= 0) {
            throw new IllegalArgumentException(String.format("PerPage must be greater than zero, got <%d>", settings.itemsPerPage()));
        }

        int pageCount;
        if (itemCount < settings.itemsPerPage()) {
            pageCount = 1;
        } else {
            pageCount = itemCount / settings.itemsPerPage();
            if (itemCount % settings.itemsPerPage() != 0) {
                pageCount++;
            }
        }

        return new PaginationInformation(
                pageCount,
                settings.itemsPerPage(),
                settings.page(),
                settings.page() < pageCount ? settings.page() + 1 : null,
                settings.page() > 1 ? settings.page() - 1 : null
        );
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getPage() {
        return page;
    }

    public Optional<Integer> getNextPage() {
        return Optional.ofNullable(nextPage);
    }

    public Optional<Integer> getPrevPage() {
        return Optional.ofNullable(prevPage);
    }
}
