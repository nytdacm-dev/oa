package com.nytdacm.oa.response.user;

import com.nytdacm.oa.entity.SocialAccount;

import java.time.Instant;

public record UserDto(
    Long userId,
    String username,
    String name,
    Instant registerTime,
    SocialAccount socialAccount
) {
    public static UserDto fromEntity(com.nytdacm.oa.entity.User user) {
        return new UserDto(
            user.getUserId(),
            user.getUsername(),
            user.getName(),
            user.getCreatedAt(),
            user.getSocialAccount()
        );
    }
}
