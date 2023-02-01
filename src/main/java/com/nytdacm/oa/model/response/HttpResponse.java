package com.nytdacm.oa.model.response;

import org.springframework.http.ResponseEntity;

public record HttpResponse<T>(
    Integer code,
    String message,
    String requestId,
    T data
) {
    public static <T> ResponseEntity<HttpResponse<T>> success(Integer code, String message, T data) {
        return ResponseEntity.status(code).body(new HttpResponse<>(code, message, null, data));
    }

    public static ResponseEntity<HttpResponse<Void>> fail(Integer code, String message, String requestId) {
        return ResponseEntity.status(code).body(new HttpResponse<>(code, message, requestId, null));
    }
}
