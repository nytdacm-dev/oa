package com.nytdacm.oa.response.group;

import com.nytdacm.oa.entity.Group;
import com.nytdacm.oa.entity.User;

import java.time.Instant;
import java.util.List;

public record GroupDto(
    Long groupId,
    String name,
    String displayName,
    List<UserDto> users,
    Boolean showInHomepage,
    Integer homepageOrder,
    Instant createdAt
) {
    public static GroupDto fromEntity(Group group) {
        return new GroupDto(
            group.getGroupId(),
            group.getName(),
            group.getDisplayName(),
            group.getUsers().parallelStream().map(UserDto::fromEntity).toList(),
            group.getShowInHomepage(),
            group.getHomepageOrder(),
            group.getCreatedAt()
        );
    }

    private record UserDto(
        Long userId,
        String username,
        String name,
        Instant registerTime
    ) {
        public static UserDto fromEntity(User user) {
            return new UserDto(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getCreatedAt()
            );
        }
    }
}

