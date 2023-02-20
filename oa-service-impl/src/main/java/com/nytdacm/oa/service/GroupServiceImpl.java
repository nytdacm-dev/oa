package com.nytdacm.oa.service;

import com.nytdacm.oa.dao.GroupDao;
import com.nytdacm.oa.dao.UserDao;
import com.nytdacm.oa.entity.Group;
import com.nytdacm.oa.exception.OaBaseException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {
    private final GroupDao groupDao;
    private final UserDao userDao;

    public GroupServiceImpl(GroupDao groupDao, UserDao userDao) {
        this.groupDao = groupDao;
        this.userDao = userDao;
    }

    private Example<Group> paramsToExample(String name, Boolean showInHomepage) {
        var probe = new Group();
        probe.setName(name);
        probe.setShowInHomepage(showInHomepage);
        probe.setUsers(null);
        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreNullValues()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreCase();
        return Example.of(probe, matcher);
    }

    @Override
    public Group getGroupById(Long id) {
        return groupDao.findById(id).orElseThrow(() -> new OaBaseException("群组不存在", 404));
    }

    @Override
    public List<Group> getAllGroups(String name, Boolean showInHomepage, int page, int size) {
        var example = paramsToExample(name, showInHomepage);
        var sort = Sort.by(Sort.Direction.ASC, "groupId");
        return groupDao.findAll(example, PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public List<Group> getAllGroups(Boolean showInHomepage) {
        return getAllGroups(null, showInHomepage, 0, Integer.MAX_VALUE);
    }

    @Override
    public long count(String name, Boolean showInHomepage) {
        var example = paramsToExample(name, showInHomepage);
        return groupDao.count(example);
    }

    @Override
    public Group newGroup(Group group) {
        return groupDao.save(group);
    }

    @Override
    public void updateGroup(Group group) {
        groupDao.save(group);
    }

    @Override
    public void delete(Long id) {
        var group = groupDao.findById(id).orElseThrow(() -> new OaBaseException("群组不存在", 404));
        var users = group.getUsers();
        users.forEach(user -> user.getGroups().remove(group));
        userDao.saveAll(users);
        groupDao.deleteById(id);
    }
}
