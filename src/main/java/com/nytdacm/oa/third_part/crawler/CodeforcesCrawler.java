package com.nytdacm.oa.third_part.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytdacm.oa.dao.UserDao;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class CodeforcesCrawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeforcesCrawler.class);

    private final UserDao userDao;

    public CodeforcesCrawler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Scheduled(cron = "0 0 */12 * * *", zone = "Asia/Shanghai")
    public void run() throws IOException {
        // TODO: 添加 cf 账号验证
        LOGGER.info("开始爬取 Codeforces 数据");
        var users = userDao.findAll().stream()
            .filter(user -> StringUtils.isNotBlank(user.getSocialAccount().getCodeforces()))
            .toList();
        var accounts = users.stream().map(user -> user.getSocialAccount().getCodeforces()).collect(Collectors.joining(";"));
        var mapper = new ObjectMapper();
        var result = mapper.readValue(
                new URL("https://codeforces.com/api/user.info?handles=" + accounts), CodeforcesUserInfoResult.class)
            .result();
        for (int i = 0; i < result.size(); i++) {
            var user = users.get(i);
            var codeforcesUser = result.get(i);
            if (user.getSocialAccount().getCodeforces().equals(codeforcesUser.handle())) {
                user.getSocialAccount().setCodeforcesRank(codeforcesUser.rank());
                user.getSocialAccount().setCodeforcesMaxRating(codeforcesUser.maxRating());
                user.getSocialAccount().setCodeforcesRating(codeforcesUser.rating());
            }
        }
        userDao.saveAll(users);
        LOGGER.info("Codeforces 数据爬取成功");
    }
}

record CodeforcesUserInfoResult(
    String status,
    List<CodeforcesUser> result
) {
    record CodeforcesUser(
        int contribution,
        long lastOnlineTimeSeconds,
        int rating,
        int friendOfCount,
        String titlePhoto,
        String rank,
        String handle,
        int maxRating,
        String avatar,
        long registrationTimeSeconds,
        String maxRank
    ) {
    }
}
