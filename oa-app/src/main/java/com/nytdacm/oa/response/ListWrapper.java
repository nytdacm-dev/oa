package com.nytdacm.oa.response;

public record ListWrapper<T>(
    long total,
    Iterable<T> data
) {
}
