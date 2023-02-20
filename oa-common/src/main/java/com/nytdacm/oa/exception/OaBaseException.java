package com.nytdacm.oa.exception;

public class OaBaseException extends RuntimeException {
    private final Integer code;

    public OaBaseException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
