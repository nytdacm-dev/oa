package com.nytdacm.oa.service;

import com.nytdacm.oa.entity.Group;

import java.util.List;

public interface GroupService {
    Group getGroupById(Long id);

    List<Group> getAllGroups(String name, Boolean showInHomepage, int page, int size);

    List<Group> getAllGroups(Boolean showInHomepage);

    long count(String name, Boolean showInHomepage);

    Group newGroup(Group group);

    void updateGroup(Group group);

    void delete(Long id);
}
