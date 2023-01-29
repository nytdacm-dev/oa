package com.nytdacm.oa.model.response.user;

import com.nytdacm.oa.model.entity.SocialAccount;

import java.time.Instant;

public record UserDto(
    Long userId,
    String username,
    String name,
    boolean superAdmin,
    boolean admin,
    Instant registerTime,
    SocialAccount socialAccount
) {
    public static UserDto fromEntity(com.nytdacm.oa.model.entity.User user) {
        return new UserDto(
            user.getUserId(),
            user.getUsername(),
            user.getName(),
            user.isSuperAdmin(),
            user.isAdmin(),
            user.getCreatedAt(),
            user.getSocialAccount()
        );
    }
}
