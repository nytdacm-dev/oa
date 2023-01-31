package com.nytdacm.oa.model.response;

public record ListWrapper<T>(
    long total,
    Iterable<T> data
) {
}
