package com.bhegstam.measurement.db;

import java.util.Optional;

public class PaginationInformation {
    private final int totalItemCount;
    private final int pageCount;
    private final int perPage;
    private final int page;
    private final Integer nextPage;
    private final Integer prevPage;

    public PaginationInformation(int totalItemCount, int pageCount, int perPage, int page, Integer nextPage, Integer prevPage) {
        this.totalItemCount = totalItemCount;
        this.pageCount = pageCount;
        this.perPage = perPage;
        this.page = page;
        this.nextPage = nextPage;
        this.prevPage = prevPage;
    }

    public static PaginationInformation calculate(int totalItemCount, PaginationSettings settings) {
        if (settings.itemsPerPage() <= 0) {
            throw new IllegalArgumentException(String.format("PerPage must be greater than zero, got <%d>", settings.itemsPerPage()));
        }

        int pageCount;
        if (totalItemCount < settings.itemsPerPage()) {
            pageCount = 1;
        } else {
            pageCount = totalItemCount / settings.itemsPerPage();
            if (totalItemCount % settings.itemsPerPage() != 0) {
                pageCount++;
            }
        }

        return new PaginationInformation(
                totalItemCount,
                pageCount,
                settings.itemsPerPage(),
                settings.page(),
                settings.page() < pageCount ? settings.page() + 1 : null,
                settings.page() > 1 ? settings.page() - 1 : null
        );
    }

    public int getTotalItemCount() {
        return totalItemCount;
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

    public int getOffset() {
        return (page - 1) * perPage;
    }

    @Override
    public String toString() {
        return "PaginationInformation{" +
                "pageCount=" + pageCount +
                ", perPage=" + perPage +
                ", page=" + page +
                ", nextPage=" + nextPage +
                ", prevPage=" + prevPage +
                '}';
    }
}
