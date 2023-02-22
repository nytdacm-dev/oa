package com.nytdacm.oa.service

import com.nytdacm.oa.dao.GroupDao
import com.nytdacm.oa.dao.SubmissionDao
import com.nytdacm.oa.dao.UserDao
import com.nytdacm.oa.entity.User
import com.nytdacm.oa.exception.OaBaseException
import spock.lang.Specification

class UserServiceImplTest extends Specification {
    def userDao = Mock(UserDao)
    def groupDao = Mock(GroupDao)
    def submissionDao = Mock(SubmissionDao)
    def userService = new UserServiceImpl(userDao, groupDao, submissionDao)

    def "test getUserById(Long): 获取ID根据已存在的用户"() {
        given: "设置用户信息"
        def user1 = new User()
        user1.setUserId(1L)
        user1.setUsername("test1")
        user1.setName("测试1")
        user1.setPasswordSalt("123")
        user1.setPassword("123")

        and: "mock userDao 返回值"
        userDao.findById(1L) >> Optional.of(user1)

        when: "调用getUserById"
        def response = userService.getUserById(1L)

        then: "判断结果"
        response == user1
    }

    def "test getUserById(Long): 根据ID获取不存在的用户"() {
        given: "mock userDao 返回值"
        userDao.findById(1L) >> Optional.empty()

        when: "调用getUserById"
        def response = userService.getUserById(1L)

        then: "判断结果"
        def ex = thrown(OaBaseException)
        ex.message == "用户不存在"
        response == null
    }
}
