package com.nytdacm.oa.service.impl;

import com.nytdacm.oa.dao.GroupDao;
import com.nytdacm.oa.service.GroupService;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {
    private final GroupDao groupDao;

    @Inject
    public GroupServiceImpl(GroupDao groupDao) {
        this.groupDao = groupDao;
    }
}
