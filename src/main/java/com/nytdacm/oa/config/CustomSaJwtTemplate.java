package com.nytdacm.oa.config;

import cn.dev33.satoken.jwt.SaJwtTemplate;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.jwt.JWT;

import java.util.Date;
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

        // TODO: 去除 SaToken 相关的payload（需要重写验证逻辑）
        JWT jwt = JWT.create()
            .setIssuer("https://oa.nytdacm.com")
            .setIssuedAt(new Date(currentTimestamp))
            .setNotBefore(new Date(currentTimestamp))
            .setExpiresAt(new Date(expTime))
            .setJWTId(UUID.randomUUID().toString())
            .setSubject(loginId.toString())
            .setPayload(LOGIN_TYPE, loginType)
            .setPayload(LOGIN_ID, loginId)
            .setPayload(DEVICE, device)
            .setPayload(EFF, expTime)
            .setPayload(RN_STR, SaFoxUtil.getRandomString(32))
            .addPayloads(extraData);

        return generateToken(jwt, keyt);
    }
}
