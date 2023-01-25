package com.nytdacm.oa.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.nytdacm.oa.model.response.HttpResponse;
import com.nytdacm.oa.model.response.user.UserDto;
import com.nytdacm.oa.service.UserService;
import com.nytdacm.oa.util.PasswordUtil;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping
    @SaCheckLogin
    public ResponseEntity<HttpResponse<UserDto>> update(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        var user = userService.getUserById(StpUtil.getLoginIdAsLong());
        if (StringUtils.isNotEmpty(userUpdateRequest.password())) {
            user.setPassword(PasswordUtil.hashPassword(userUpdateRequest.password()));
        }
        if (StringUtils.isNotEmpty(userUpdateRequest.name())) {
            user.setName(userUpdateRequest.name());
        }
        var newUser = userService.updateUser(user);
        return HttpResponse.success(200, "更新成功", UserDto.fromEntity(newUser));
    }
}

record UserUpdateRequest(
    @Size(min = 6, message = "密码长度至少为6位") String password,
    @Size(max = 6, message = "姓名长度不能超过6位") String name
) {
}
