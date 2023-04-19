package com.nytdacm.oa.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.nytdacm.oa.response.HttpResponse;
import com.nytdacm.oa.response.user.UserDto;
import com.nytdacm.oa.service.UserService;
import com.nytdacm.oa.utils.PasswordUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
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
@SaCheckLogin
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
    public ResponseEntity<HttpResponse<UserDto>> update(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        var user = userService.getUserById(StpUtil.getLoginIdAsLong());
        if (StringUtils.isNotEmpty(userUpdateRequest.password())) {
            var salt = PasswordUtil.getSalt();
            user.setPasswordSalt(salt);
            user.setPassword(PasswordUtil.hashPassword(userUpdateRequest.password(), salt));
        }
        if (userUpdateRequest.codeforces() != null) {
            user.getSocialAccount().setCodeforces(userUpdateRequest.codeforces());
            user.getUserInternal().setCodeforcesCrawlerEnabled(false);
        }
        if (userUpdateRequest.atCoder() != null) {
            user.getSocialAccount().setAtCoder(userUpdateRequest.atCoder());
            user.getUserInternal().setAtcoderCrawlerEnabled(false);
        }
        if (userUpdateRequest.luogu() != null) {
            user.getSocialAccount().setLuogu(userUpdateRequest.luogu());
        }
        if (userUpdateRequest.nowcoder() != null) {
            user.getSocialAccount().setNowcoder(userUpdateRequest.nowcoder());
            user.getUserInternal().setNowcoderCrawlerEnabled(false);
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
    String codeforces,
    String atCoder,
    String luogu,
    String nowcoder,
    String vjudge
) {
}
