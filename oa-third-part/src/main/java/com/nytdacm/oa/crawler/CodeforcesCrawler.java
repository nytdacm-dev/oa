package com.nytdacm.oa.crawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytdacm.oa.dao.UserDao;
import com.nytdacm.oa.entity.Submission;
import com.nytdacm.oa.entity.User;
import org.apache.commons.lang.StringUtils;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CodeforcesCrawler implements OJCrawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeforcesCrawler.class);

    private final UserDao userDao;

    public CodeforcesCrawler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Scheduled(cron = "56 34 5/12 * * *", zone = "Asia/Shanghai")
    public void run() throws IOException {
        // TODO: 重构
        LOGGER.info("开始爬取 Codeforces 数据");
        var users = userDao.findAll().parallelStream()
            .filter(user -> StringUtils.isNotBlank(user.getSocialAccount().getCodeforces()) &&
                Boolean.TRUE.equals(user.getUserInternal().getCodeforcesCrawlerEnabled()))
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

    @Override
    public List<Submission> crawl(String ojUser, long id, User user) {
        try (var httpclient = HttpClients.createDefault()) {
            var request = ClassicRequestBuilder
                .get("https://codeforces.com/api/user.status")
                .addParameter("handle", ojUser)
                .addParameter("from", "1")
                .addParameter("count", "200")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .build();
            return httpclient.execute(request, response -> {
                if (response.getCode() < 200 || response.getCode() >= 300) {
                    return List.of();
                }
                var data = response.getEntity().getContent();
                return parse(data, id, user);
            });
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public List<Submission> parse(Object data, long id, User user) {
        var mapper = new ObjectMapper();
        try {
            var response = mapper.readValue((InputStream) data, CodeforcesSubmissionResult.class);
            if (!"OK".equals(response.status()) && response.result().size() > 0) {
                return List.of();
            }
            return response.result().parallelStream()
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
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public boolean check(String user) {
        try (var httpclient = HttpClients.createDefault()) {
            var request = ClassicRequestBuilder.get("https://codeforces.com/api/user.info")
                .addParameter("handles", user)
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .build();
            return httpclient.execute(request, response -> {
                if (response.getCode() < 200 || response.getCode() >= 300) {
                    return false;
                }
                var mapper = new ObjectMapper();
                var result = mapper.readValue(response.getEntity().getContent(), CodeforcesUserInfoResult.class);
                return "OK".equals(result.status()) && result.result().size() == 1 && user.equals(result.result().get(0).handle());
            });
        } catch (IOException e) {
            return false;
        }
    }

    private record CodeforcesUserInfoResult(
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
    private record CodeforcesSubmissionResult(
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
}

