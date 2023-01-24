package com.nytdacm.oa;

import cn.dev33.satoken.jwt.SaJwtUtil;
import com.nytdacm.oa.config.CustomSaJwtTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OaApplication {

    public static void main(String[] args) {
        SaJwtUtil.setSaJwtTemplate(new CustomSaJwtTemplate());
        SpringApplication.run(OaApplication.class, args);
    }

}
