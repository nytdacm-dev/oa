package com.nytdacm.oa.config;

import cn.dev33.satoken.jwt.SaJwtTemplate;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.jwt.JWT;

import java.util.Map;
import java.util.UUID;

public class CustomSaJwtTemplate extends SaJwtTemplate {
    @Override
    public String createToken(String loginType, Object loginId, String device,
                              long timeout, Map<String, Object> extraData, String keyt) {
        var currentTimestamp = System.currentTimeMillis();
        long expTime = timeout;
        if (timeout != NEVER_EXPIRE) {
            expTime = timeout * 1000 + currentTimestamp;
        }

        JWT jwt = JWT.create()
            .setPayload(LOGIN_TYPE, loginType)
            .setPayload(LOGIN_ID, loginId)
            .setPayload(DEVICE, device)
            .setPayload(EFF, expTime)
            .setPayload(RN_STR, SaFoxUtil.getRandomString(32))
            .setPayload("iss", "https://oa.nytdacm.com")
            .setPayload("iat", currentTimestamp)
            .setPayload("exp", expTime)
            .setPayload("nbf", currentTimestamp)
            .setPayload("jti", UUID.randomUUID().toString())
            .setPayload("sub", loginId)
            .addPayloads(extraData);

        return generateToken(jwt, keyt);
    }
}
