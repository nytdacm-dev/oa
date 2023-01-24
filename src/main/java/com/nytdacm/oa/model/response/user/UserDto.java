package com.nytdacm.oa.model.response.user;

public record UserDto(
    Long userId,
    String username,
    String name
) {
    public static UserDto fromEntity(com.nytdacm.oa.model.entity.User user) {
        return new UserDto(
            user.getUserId(),
            user.getUsername(),
            user.getName()
        );
    }
}
