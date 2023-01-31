package com.nytdacm.oa.config;

import cn.dev33.satoken.stp.StpInterface;
import com.nytdacm.oa.service.UserService;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component    // 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {
    private final UserService userService;

    @Inject
    public StpInterfaceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        var userId = Long.parseLong(loginId.toString());
        var user = userService.getUserById(userId);
        if (user.getAdmin()) {
            list.add("admin");
        }
        if (user.getSuperAdmin()) {
            list.add("super-admin");
            list.add("admin");
        }
        list.add("user");
        return list;
    }
}
