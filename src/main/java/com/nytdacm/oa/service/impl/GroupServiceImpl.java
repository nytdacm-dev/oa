package com.nytdacm.oa.service.impl;

import com.nytdacm.oa.dao.GroupDao;
import com.nytdacm.oa.model.entity.Group;
import com.nytdacm.oa.service.GroupService;
import jakarta.inject.Inject;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {
    private final GroupDao groupDao;

    @Inject
    public GroupServiceImpl(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    private Example<Group> paramsToExample(String name) {
        var probe = new Group();
        probe.setName(name);
        probe.setUsers(null);
        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreNullValues()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreCase();
        return Example.of(probe, matcher);
    }

    @Override
    public List<Group> getAllGroups(String name, int page, int size) {
        var example = paramsToExample(name);
        var sort = Sort.by(Sort.Direction.ASC, "groupId");
        return groupDao.findAll(example, PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public long count(String name) {
        var example = paramsToExample(name);
        return groupDao.count(example);
    }

    @Override
    public Group newGroup(Group group) {
        return groupDao.save(group);
    }
}
