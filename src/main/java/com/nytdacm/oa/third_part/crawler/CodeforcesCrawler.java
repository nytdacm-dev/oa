package com.nytdacm.oa.third_part.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytdacm.oa.dao.UserDao;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

// TODO: 优化爬虫
@Component
@Transactional
public class CodeforcesCrawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeforcesCrawler.class);

    private final UserDao userDao;
    private final ObjectMapper mapper;
    private final OkHttpClient client;

    @Inject
    public CodeforcesCrawler(UserDao userDao, ObjectMapper mapper, OkHttpClient client) {
        this.userDao = userDao;
        this.mapper = mapper;
        this.client = client;
    }

    @Scheduled(cron = "0 0 */6 * * *", zone = "Asia/Shanghai")
    public void run() {
        LOGGER.info("开始爬取 Codeforces 数据");
        var users = userDao.findAll().stream()
            .filter(user -> StringUtils.isNotBlank(user.getSocialAccount().getCodeforces()) &&
                user.getSocialAccount().isCodeforcesCrawlerEnabled())
            .toList();
        var accounts = users.stream().map(user -> user.getSocialAccount().getCodeforces()).collect(Collectors.joining(";"));
        var request = new Request.Builder()
            .get()
            .url("https://codeforces.com/api/user.info?handles=" + accounts)
            .build();
        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            var result = mapper.readValue(body, CodeforcesUserInfoResult.class);
            if (!(response.code() >= 200 && response.code() < 300)) {
                LOGGER.error("抓取 Codeforces 数据失败，错误信息：%s".formatted(result.comment()));
                return;
            }
            var list = result.result();
            for (int i = 0; i < list.size(); i++) {
                var user = users.get(i);
                var codeforcesUser = list.get(i);
                if (user.getSocialAccount().getCodeforces().equals(codeforcesUser.handle())) {
                    user.getSocialAccount().setCodeforcesRank(codeforcesUser.rank());
                    user.getSocialAccount().setCodeforcesMaxRating(codeforcesUser.maxRating());
                    user.getSocialAccount().setCodeforcesRating(codeforcesUser.rating());
                }
            }
            userDao.saveAll(users);
            LOGGER.info("Codeforces 数据爬取成功");
        } catch (Exception e) {
            LOGGER.error("抓取 Codeforces 数据失败", e);
        }
    }

    @Scheduled(fixedDelay = 600000) // 10分钟
    public void checkCodeforcesAccount() {
        LOGGER.info("开始验证用户 Codeforces 账号正确性并更新值");
        var users = userDao.findAll().stream()
            .filter(user -> StringUtils.isNotBlank(user.getSocialAccount().getCodeforces()) &&
                !user.getSocialAccount().isCodeforcesCrawlerEnabled())
            .toList();
        users.forEach(user -> {
            var account = user.getSocialAccount().getCodeforces();
            var request = new Request.Builder()
                .get()
                .url("https://codeforces.com/api/user.info?handles=" + account)
                .build();
            try (var response = client.newCall(request).execute()) {
                assert response.body() != null;
                var body = response.body().string();
                var result = mapper.readValue(body, CodeforcesUserInfoResult.class);
                if (!(response.code() >= 200 && response.code() < 300)) {
                    LOGGER.error(String.format("爬取 %s 用户的 Codeforces 账号（%s）时出错，错误信息：%s",
                        user.getUsername(), account, result.comment()));
                    return;
                }
                if ("OK".equals(result.status())) {
                    user.getSocialAccount().setCodeforcesCrawlerEnabled(true);
                    user.getSocialAccount().setCodeforcesRank(result.result().get(0).rank());
                    user.getSocialAccount().setCodeforcesMaxRating(result.result().get(0).maxRating());
                    user.getSocialAccount().setCodeforcesRating(result.result().get(0).rating());
                }
            } catch (Exception e) {
                LOGGER.error(String.format("爬取 %s 用户的 Codeforces 账号（%s）时出错", user.getUsername(), account), e);
            }
        });
        LOGGER.info("验证用户 Codeforces 账号正确性完成，本次验证了 %d 个账号".formatted(users.size()));
    }
}

record CodeforcesUserInfoResult(
    String status,
    List<CodeforcesUser> result,
    String comment
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
