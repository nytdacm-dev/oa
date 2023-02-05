package com.nytdacm.oa.model.response.group;

import com.nytdacm.oa.model.entity.Group;

public record GroupDto(
    Long groupId,
    String name
) {
    public static GroupDto fromEntity(Group group) {
        return new GroupDto(
            group.getGroupId(),
            group.getName()
        );
    }
}
