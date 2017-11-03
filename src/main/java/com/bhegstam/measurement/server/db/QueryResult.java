package com.bhegstam.measurement.server.db;

import java.util.List;
import java.util.Optional;

public class QueryResult<T> {
    private final List<T> data;
    private final PaginationInformation paginationInformation;

    public QueryResult(List<T> data) {
        this(data, null);
    }

    public QueryResult(List<T> data, PaginationInformation paginationInformation) {
        this.data = data;
        this.paginationInformation = paginationInformation;
    }

    public List<T> getData() {
        return data;
    }

    public Optional<PaginationInformation> getPaginationInformation() {
        return Optional.ofNullable(paginationInformation);
    }
}
