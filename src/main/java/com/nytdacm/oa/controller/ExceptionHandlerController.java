package com.nytdacm.oa.controller;

import cn.dev33.satoken.exception.NotLoginException;
import com.nytdacm.oa.exception.OaBaseException;
import com.nytdacm.oa.model.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ExceptionHandlerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<HttpResponse<Void>> handleSaTokenNotLoginException(NotLoginException e) {
        String message = switch (e.getType()) {
            case NotLoginException.TOKEN_TIMEOUT -> "token 已过期，请重新登录";
            case NotLoginException.INVALID_TOKEN_MESSAGE -> "token 错误";
            default -> "未登录";
        };
        return HttpResponse.fail(401, message);
    }

    @ExceptionHandler(OaBaseException.class)
    public ResponseEntity<HttpResponse<Void>> handleOaException(OaBaseException e) {
        return HttpResponse.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<HttpResponse<Void>> handleOaException(DataIntegrityViolationException ignored) {
        return HttpResponse.fail(409, "数据冲突");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpResponse<Void>> handleOaException(MethodArgumentNotValidException e) {
        return HttpResponse.fail(400,
            Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<HttpResponse<Void>> handleOaException(HttpMessageConversionException ignored) {
        return HttpResponse.fail(400, "请求数据错误");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse<Void>> handleAllException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return HttpResponse.fail(500, "服务器内部错误");
    }
}
