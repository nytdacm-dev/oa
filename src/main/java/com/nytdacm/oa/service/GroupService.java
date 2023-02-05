package com.nytdacm.oa.service;

import com.nytdacm.oa.model.entity.Group;

import java.util.List;

public interface GroupService {
    List<Group> getAllGroups(String name, int page, int size);

    long count(String name);

    Group newGroup(Group group);
}
