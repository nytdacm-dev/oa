package com.nytdacm.oa.third_part.crawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytdacm.oa.dao.SubmissionDao;
import com.nytdacm.oa.dao.UserDao;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.StringUtils;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Transactional
public class LuoguCrawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LuoguCrawler.class);
    private final UserDao userDao;
    private final SubmissionDao submissionDao;

    @Inject
    public LuoguCrawler(UserDao userDao, SubmissionDao submissionDao) {
        this.userDao = userDao;
        this.submissionDao = submissionDao;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 2, zone = "Asia/Shanghai") // 2h
    public void luoguAccountCheckerCrawler() {
        LOGGER.info("开始验证用户洛谷账号正确性并更新值");
        var users = userDao.findAll().stream()
            .filter(user -> StringUtils.isNotBlank(user.getSocialAccount().getLuogu()) &&
                !Boolean.TRUE.equals(user.getSocialAccount().getLuoguCrawlerEnabled()))
            .peek(user -> {
                var account = user.getSocialAccount().getLuogu();
                try (var httpclient = HttpClients.createDefault()) {
                    var request = ClassicRequestBuilder.get("https://www.luogu.com.cn/user/" + account + "?_contentOnly")
                        .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .build();
                    httpclient.execute(request, response -> {
                        var mapper = new ObjectMapper();
                        var res = mapper.readValue(response.getEntity().getContent(), LuoguResponse.class);
                        if (res.code() >= 200 && res.code() < 300 &&
                            ((LuoguResponse.UserShowData) res.currentData()).user().uid().toString().equals(account)) {
                            user.getSocialAccount().setLuoguCrawlerEnabled(true);
                        } else {
                            user.getSocialAccount().setLuoguCrawlerEnabled(false);
                            user.getSocialAccount().setLuogu(null);
                            LOGGER.error(String.format("爬取 %s 用户的洛谷账号（%s）时出错", user.getUsername(), account));
                        }
                        return null;
                    });
                } catch (Exception e) {
                    user.getSocialAccount().setLuoguCrawlerEnabled(false);
                    user.getSocialAccount().setLuogu(null);
                    LOGGER.error(String.format("爬取 %s 用户的洛谷账号（%s）时出错", user.getUsername(), account), e);
                }
            })
            .toList();
        userDao.saveAll(users);
        LOGGER.info("洛谷账号验证成功，本次共验证了 " + users.size() + " 个账号");
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
record LuoguResponse<T>(
    Integer code,
    T currentData
) {

    public record UserShowData(
        LuoguUser user,
        List<LuoguProblem> passedProblems,
        List<LuoguProblem> submittedProblems
    ) {
    }

    public record LuoguProblem(
        String pid,
        String title,
        Integer difficulty,
        Integer fullScore,
        String type
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LuoguUser(
        Long registerTime,
        Integer ranking,
        Integer passedProblemCount,
        Integer submittedProblemCount,
        Integer uid,
        String name
    ) {
    }
}
