package com.bhegstam.measurement.server.db;

import java.util.List;

public class QueryResult<T> {
    private final List<T> data;
    private final PaginationInformation paginationInformation;

    public QueryResult(List<T> data, PaginationInformation paginationInformation) {
        this.data = data;
        this.paginationInformation = paginationInformation;
    }

    public List<T> getData() {
        return data;
    }

    public PaginationInformation getPaginationInformation() {
        return paginationInformation;
    }
}
