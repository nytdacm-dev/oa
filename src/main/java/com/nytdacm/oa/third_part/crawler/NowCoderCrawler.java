package com.nytdacm.oa.third_part.crawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytdacm.oa.dao.SubmissionDao;
import com.nytdacm.oa.dao.UserDao;
import com.nytdacm.oa.model.entity.Submission;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.StringUtils;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@Transactional
public class NowCoderCrawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NowCoderCrawler.class);
    private final UserDao userDao;
    private final SubmissionDao submissionDao;

    @Inject
    public NowCoderCrawler(UserDao userDao, SubmissionDao submissionDao) {
        this.userDao = userDao;
        this.submissionDao = submissionDao;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60, zone = "Asia/Shanghai") // 1h
    public void nowcoderAccountCheckerCrawler() {
        LOGGER.info("开始验证用户牛客账号正确性并更新值");
        var users = userDao.findAll().stream()
            .filter(user -> StringUtils.isNotBlank(user.getSocialAccount().getNowcoder()) &&
                !Boolean.TRUE.equals(user.getSocialAccount().getNowcoderCrawlerEnabled()))
            .peek(user -> {
                var account = user.getSocialAccount().getNowcoder();
                try (var httpclient = HttpClients.createDefault()) {
                    var request = ClassicRequestBuilder.get("https://gw-c.nowcoder.com/api/sparta/user/profile/" + account)
                        .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .build();
                    httpclient.execute(request, response -> {
                        var mapper = new ObjectMapper();
                        var res = mapper.readValue(response.getEntity().getContent(), NowcoderResponse.class);
                        if (Boolean.TRUE.equals(res.success())) {
                            var data = mapper.convertValue(res.data(), NowcoderResponse.NowcoderAccountData.class);
                            if (account.equals(data.id().toString())) {
                                user.getSocialAccount().setNowcoderCrawlerEnabled(true);
                            } else {
                                user.getSocialAccount().setNowcoderCrawlerEnabled(false);
                                user.getSocialAccount().setNowcoder(null);
                                LOGGER.error(String.format("爬取 %s 用户的牛客账号（%s）时出错", user.getUsername(), account));
                            }
                        }
                        return null;
                    });
                } catch (Exception e) {
                    user.getSocialAccount().setNowcoderCrawlerEnabled(false);
                    user.getSocialAccount().setNowcoder(null);
                    LOGGER.error(String.format("爬取 %s 用户的牛客账号（%s）时出错", user.getUsername(), account), e);
                }
            })
            .toList();
        userDao.saveAll(users);
        LOGGER.info("牛客账号验证成功，本次共验证了 " + users.size() + " 个账号");
    }

    @Scheduled(cron = "0 0 3/12 * * *", zone = "Asia/Shanghai")
    public void nowcoderSubmissionCrawler() {
        LOGGER.info("开始爬取牛客提交记录");
        userDao.findAll().stream()
            .filter(user -> StringUtils.isNotBlank(user.getSocialAccount().getNowcoder()) && Boolean.TRUE.equals(user.getSocialAccount().getNowcoderCrawlerEnabled()))
            .forEach(user -> {
                var account = user.getSocialAccount().getNowcoder();
                var url = "https://ac.nowcoder.com/acm/contest/profile/" + account + "/practice-coding?pageSize=200";
                try {
                    var document = Jsoup.connect(url).header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36").get();
                    var submissions = document.select("body > div.nk-container.acm-container > div.nk-container > div.nk-main.with-profile-menu.clearfix > section > table > tbody > tr");
                    var list = submissions.stream().map(submission -> {
                        var submissionId = submission.select("td:nth-child(1) > a").stream().filter(element -> element.attr("href").contains("acm/contest/view-submission")).findFirst().get().text();
                        var problem = submission.select("td:nth-child(2) > a").stream().filter(element -> element.attr("href").contains("acm/problem")).findFirst().get();
                        var problemName = problem.text();
                        var problemId = problem.attr("href").replace("/acm/problem/", "");
                        var result = submission.select("td:nth-child(3) > a").stream().filter(element -> element.attr("href").contains("acm/contest/view-submission")).findFirst().get().text();
                        result = switch (result) {
                            case "答案正确" -> Submission.STATUS_SUCCESS;
                            case "答案错误" -> Submission.STATUS_WRONG_ANSWER;
                            case "运行超时" -> Submission.STATUS_TIME_LIMIT_EXCEEDED;
                            case "内存超限" -> Submission.STATUS_MEMORY_LIMIT_EXCEEDED;
                            case "编译错误" -> Submission.STATUS_COMPILATION_ERROR;
                            default -> Submission.STATUS_RUNTIME_ERROR;
                        };
                        var language = submission.select("td:nth-child(8)").text();
                        var time = submission.select("td:nth-child(9)").text();
                        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        var timeInstant = LocalDateTime.parse(time, formatter).toInstant(ZoneOffset.ofHours(8));

                        var eSubmission = new Submission();
                        eSubmission.setOj(Submission.OJ_NOWCODER);
                        eSubmission.setUser(user);
                        eSubmission.setRemoteSubmissionId(submissionId);
                        eSubmission.setName(problemName);
                        eSubmission.setRemoteProblemId(problemId);
                        eSubmission.setStatus(result);
                        eSubmission.setLanguage(language);
                        eSubmission.setSubmitTime(timeInstant);
                        return eSubmission;
                    }).filter(submission -> {
                        var id = Long.parseLong(submission.getRemoteSubmissionId());
                        var lastId = user.getUserInternal().getLastNowcoderSubmissionId();
                        return id > lastId;
                    }).toList();
                    if (!list.isEmpty()) {
                        user.getUserInternal().setLastNowcoderSubmissionId(Long.parseLong(list.get(0).getRemoteSubmissionId()));
                        user.getSubmissions().addAll(list);
                        userDao.save(user);
                    }
                } catch (Exception e) {
                    LOGGER.error(String.format("爬取 %s 用户的牛客提交记录（%s）时出错", user.getUsername(), account), e);
                }
            });
        LOGGER.info("牛客提交记录爬取成功");
    }
}

record NowcoderResponse<T>(
    Boolean success,
    Integer code,
    String msg,
    T data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NowcoderAccountData(
        Integer id,
        String nickname,
        String introduction,
        Integer workType,
        Integer workStatusDetail,
        String workTime,
        String eduLevel,
        String educationInfo
    ) {
    }
}
