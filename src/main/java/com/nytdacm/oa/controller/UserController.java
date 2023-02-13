package com.nytdacm.oa.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.nytdacm.oa.model.response.HttpResponse;
import com.nytdacm.oa.model.response.user.UserDto;
import com.nytdacm.oa.service.UserService;
import com.nytdacm.oa.util.PasswordUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    @Cacheable(value = "user", key = "#username")
    public ResponseEntity<HttpResponse<UserDto>> getUser(@PathVariable("username") String username) {
        var user = userService.getUserByUsername(username);
        return HttpResponse.success(200, "获取成功", UserDto.fromEntity(user));
    }

    @PatchMapping
    @SaCheckLogin
    public ResponseEntity<HttpResponse<UserDto>> update(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        var user = userService.getUserById(StpUtil.getLoginIdAsLong());
        if (StringUtils.isNotEmpty(userUpdateRequest.password())) {
            var salt = PasswordUtil.getSalt();
            user.setPasswordSalt(salt);
            user.setPassword(PasswordUtil.hashPassword(userUpdateRequest.password(), salt));
        }
        if (StringUtils.isNotEmpty(userUpdateRequest.name())) {
            user.setName(userUpdateRequest.name());
        }
        if (userUpdateRequest.codeforces() != null) {
            user.getSocialAccount().setCodeforces(userUpdateRequest.codeforces());
            user.getSocialAccount().setCodeforcesCrawlerEnabled(false);
        }
        if (userUpdateRequest.github() != null) {
            user.getSocialAccount().setGithub(userUpdateRequest.github());
        }
        if (userUpdateRequest.website() != null) {
            user.getSocialAccount().setWebsite(userUpdateRequest.website());
        }
        if (userUpdateRequest.atCoder() != null) {
            user.getSocialAccount().setAtCoder(userUpdateRequest.atCoder());
            user.getUserInternal().setAtcoderCrawlerEnabled(false);
        }
        if (userUpdateRequest.poj() != null) {
            user.getSocialAccount().setPoj(userUpdateRequest.poj());
            user.getUserInternal().setPojCrawlerEnabled(false);
        }
        if (userUpdateRequest.luogu() != null) {
            user.getSocialAccount().setLuogu(userUpdateRequest.luogu());
            user.getSocialAccount().setLuoguCrawlerEnabled(false);
        }
        if (userUpdateRequest.nowcoder() != null) {
            user.getSocialAccount().setNowcoder(userUpdateRequest.nowcoder());
            user.getSocialAccount().setNowcoderCrawlerEnabled(false);
        }
        if (userUpdateRequest.vjudge() != null) {
            user.getSocialAccount().setVjudge(userUpdateRequest.vjudge());
            user.getUserInternal().setVjudgeCrawlerEnabled(false);
        }
        var newUser = userService.updateUser(user);
        return HttpResponse.success(200, "更新成功", UserDto.fromEntity(newUser));
    }
}

record UserUpdateRequest(
    @Size(min = 6, message = "密码长度至少为6位") String password,
    @Size(max = 6, message = "姓名长度不能超过6位") String name,
    String codeforces,
    String github,
    String website,
    String atCoder,
    String luogu,
    String nowcoder,
    String poj,
    String vjudge
) {
}
