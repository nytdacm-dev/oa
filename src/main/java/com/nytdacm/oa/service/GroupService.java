package com.nytdacm.oa.service;

import com.nytdacm.oa.model.entity.Group;

import java.util.List;

public interface GroupService {
    List<Group> getAllGroups(String name, Boolean showInHomepage, int page, int size);

    long count(String name, Boolean showInHomepage);

    Group newGroup(Group group);

    void delete(Long id);
}
