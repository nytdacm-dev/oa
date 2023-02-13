package com.nytdacm.oa.third_part.crawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytdacm.oa.dao.UserDao;
import com.nytdacm.oa.model.entity.Submission;
import com.nytdacm.oa.model.entity.User;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
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

    @Scheduled(cron = "56 34 5/12 * * *", zone = "Asia/Shanghai")
    public void run() throws IOException {
        LOGGER.info("开始爬取 Codeforces 数据");
        var users = userDao.findAll().parallelStream()
            .filter(user -> StringUtils.isNotBlank(user.getSocialAccount().getCodeforces()) &&
                Boolean.TRUE.equals(user.getSocialAccount().getCodeforcesCrawlerEnabled()))
            .toList();
        var accounts = users.parallelStream().map(user -> user.getSocialAccount().getCodeforces()).collect(Collectors.joining(";"));
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

    @Scheduled(fixedDelay = 1920000) // 30分钟左右
    public void checkCodeforcesAccount() {
        // TODO: 改用 HTTP 请求库
        LOGGER.info("开始验证用户 Codeforces 账号正确性并更新值");
        var users = userDao.findAll().parallelStream()
            .filter(user -> StringUtils.isNotBlank(user.getSocialAccount().getCodeforces()) &&
                Boolean.FALSE.equals(user.getSocialAccount().getCodeforcesCrawlerEnabled()))
            .toList();
        users.forEach(user -> {
            var mapper = new ObjectMapper();
            var account = user.getSocialAccount().getCodeforces();
            try {
                var result = mapper.readValue(
                    new URL("https://codeforces.com/api/user.info?handles=" + account),
                    CodeforcesUserInfoResult.class);
                if ("OK".equals(result.status()) && result.result().size() == 1 && account.equals(result.result().get(0).handle())) {
                    user.getSocialAccount().setCodeforcesCrawlerEnabled(true);
                    user.getSocialAccount().setCodeforcesRank(result.result().get(0).rank());
                    user.getSocialAccount().setCodeforcesMaxRating(result.result().get(0).maxRating());
                    user.getSocialAccount().setCodeforcesRating(result.result().get(0).rating());
                    userDao.save(user);
                }
            } catch (Exception e) {
                user.getSocialAccount().setCodeforces(null);
                // TODO: 添加提示
                LOGGER.error(String.format("爬取 %s 用户的 Codeforces 账号（%s）时出错", user.getUsername(), account), e);
            }
        });
        LOGGER.info("验证用户 Codeforces 账号正确性完成，本次验证了 %d 个账号".formatted(users.size()));
    }

    @Scheduled(cron = "0 6 9/6 * * *", zone = "Asia/Shanghai")
    public void getCodeforcesSubmissions() {
        // TODO: 重写逻辑
        LOGGER.info("开始爬取 Codeforces 提交记录");
        var users = userDao.findAll().parallelStream()
            .filter(user -> StringUtils.isNotBlank(user.getSocialAccount().getCodeforces()) &&
                Boolean.TRUE.equals(user.getSocialAccount().getCodeforcesCrawlerEnabled()))
            .toList();
        users.forEach(user -> {
            var account = user.getSocialAccount().getCodeforces();
            try {
                var mapper = new ObjectMapper();
                var result = mapper.readValue(
                    new URL("https://codeforces.com/api/user.status?handle=" + account + "&from=1&count=200"),
                    CodeforcesSubmissionResult.class);
                if ("OK".equals(result.status()) && result.result().size() > 0) {
                    if (user.getUserInternal() == null) {
                        user.setUserInternal(new User.UserInternal());
                    }
                    var submissions = result.result().parallelStream()
                        .filter(submission -> submission.id() > user.getUserInternal().getLastCodeforcesSubmissionId())
                        .map(submission -> {
                            var s = new Submission();
                            s.setUser(user);
                            s.setLanguage(submission.programmingLanguage());
                            s.setContestId(String.valueOf(submission.contestId()));
                            if (submission.contestId() > 100000) {
                                s.setOj(Submission.OJ_CODEFORCES_GYM);
                            } else {
                                s.setOj(Submission.OJ_CODEFORCES);
                            }
                            s.setStatus(submission.verdict());
                            s.setName(submission.problem().name());
                            s.setRemoteSubmissionId(String.valueOf(submission.id()));
                            s.setRemoteProblemId(submission.problem().contestId() + submission.problem().index());
                            s.setSubmitTime(Instant.ofEpochSecond(submission.creationTimeSeconds()));
                            s.setRelativeTime(submission.relativeTimeSeconds());
                            return s;
                        })
                        .toList();
                    if (submissions.size() > 0) {
                        user.getSubmissions().addAll(submissions);
                        user.getUserInternal().setLastCodeforcesSubmissionId(result.result().get(0).id());
                        userDao.save(user);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(String.format("爬取 %s 用户的 Codeforces 账号（%s）时出错", user.getUsername(), account), e);
            }
        });
        LOGGER.info("Codeforces 提交记录爬取成功");
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

@JsonIgnoreProperties(ignoreUnknown = true)
record CodeforcesSubmissionResult(
    String status,
    List<CodeforcesSubmission> result
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CodeforcesSubmission(
        Long id,
        Long contestId,
        Long creationTimeSeconds,
        Long relativeTimeSeconds,
        CodeforcesProblem problem,
        String programmingLanguage,
        String verdict
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CodeforcesProblem(
            String index,
            Long contestId,
            String name
        ) {
        }
    }
}
