package com.nytdacm.oa.controller;

import cn.dev33.satoken.exception.NotLoginException;
import com.nytdacm.oa.model.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {
    private final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<HttpResponse<Void>> handleSaTokenNotLoginException(NotLoginException e) {
        String message = switch (e.getType()) {
            case NotLoginException.TOKEN_TIMEOUT -> "token 已过期，请重新登录";
            case NotLoginException.INVALID_TOKEN_MESSAGE -> "token 错误";
            default -> "未登录";
        };
        return HttpResponse.fail(401, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse<Void>> handleAllException(Exception e) {
        logger.error(e.getMessage(), e);
        return HttpResponse.fail(500, "服务器内部错误");
    }
}
