package com.nytdacm.oa.controller;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
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
import java.util.UUID;

@RestControllerAdvice
public class ExceptionHandlerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(SaTokenException.class)
    public ResponseEntity<HttpResponse<Void>> handleSaTokenException(SaTokenException e) {
        if (e instanceof NotLoginException nle) {
            String message = switch (nle.getType()) {
                case NotLoginException.TOKEN_TIMEOUT -> "token 已过期，请重新登录";
                case NotLoginException.INVALID_TOKEN_MESSAGE -> "token 错误";
                default -> "未登录";
            };
            return HttpResponse.fail(401, message, null);
        } else if (e instanceof NotRoleException) {
            return HttpResponse.fail(403, "无权限", null);
        }
        return handleAllException(e);
    }

    @ExceptionHandler(OaBaseException.class)
    public ResponseEntity<HttpResponse<Void>> handleOaException(OaBaseException e) {
        return HttpResponse.fail(e.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<HttpResponse<Void>> handleOaException(DataIntegrityViolationException e) {
        var uuid = UUID.randomUUID().toString();
        LOGGER.error(String.format("[%s] %s", uuid, e.getMessage()), e);
        return HttpResponse.fail(409, "数据冲突", uuid);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpResponse<Void>> handleOaException(MethodArgumentNotValidException e) {
        return HttpResponse.fail(400,
            Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage(), null);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<HttpResponse<Void>> handleOaException(HttpMessageConversionException e) {
        var uuid = UUID.randomUUID().toString();
        LOGGER.error(String.format("[%s] %s", uuid, e.getMessage()), e);
        return HttpResponse.fail(400, "请求数据错误", uuid);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse<Void>> handleAllException(Exception e) {
        var uuid = UUID.randomUUID().toString();
        LOGGER.error(String.format("[%s] %s", uuid, e.getMessage()), e);
        return HttpResponse.fail(500, "服务器内部错误", uuid);
    }
}
