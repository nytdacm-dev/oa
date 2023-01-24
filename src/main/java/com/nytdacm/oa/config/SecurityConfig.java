package com.nytdacm.oa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
    @Value("${sa-token.token-expire-timeout:604800}")
    private final Long tokenExpireTime = 604800L;

    public Long getTokenExpireTime() {
        return tokenExpireTime;
    }
}
