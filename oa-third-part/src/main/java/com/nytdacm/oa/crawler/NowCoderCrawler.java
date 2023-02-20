package com.nytdacm.oa.crawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytdacm.oa.entity.Submission;
import com.nytdacm.oa.entity.User;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class NowCoderCrawler implements OJCrawler {
    @Override
    public List<Submission> crawl(String ojUser, long id, User user) {
        var account = user.getSocialAccount().getNowcoder();
        var url = "https://ac.nowcoder.com/acm/contest/profile/" + account + "/practice-coding?pageSize=200";
        try (var httpclient = HttpClients.createDefault()) {
            var request = ClassicRequestBuilder
                .get(url)
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .build();
            return httpclient.execute(request, response -> {
                var data = response.getEntity().getContent();
                return parse(data, id, user);
            });
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public List<Submission> parse(Object data, long id, User user) {
        try {
            var document = Jsoup.parse((InputStream) data, "UTF-8", "https://ac.nowcoder.com");
            var elements = document.select("body > div.nk-container.acm-container > div.nk-container > div.nk-main.with-profile-menu.clearfix > section > table > tbody > tr");
            return elements.parallelStream()
                .filter(submission -> submission.childrenSize() == 9)
                .filter(submission -> Long.parseLong(submission.child(0).text()) > id)
                .map(submission -> {
                    var remoteSubmissionId = submission.child(0).text();
                    var problem = submission.child(1).child(0);
                    var problemName = problem.text();
                    var problemId = problem.attr("href").replace("/acm/problem/", "");
                    var result = submission.child(2).text();
                    result = switch (result) {
                        case "答案正确" -> Submission.STATUS_SUCCESS;
                        case "答案错误" -> Submission.STATUS_WRONG_ANSWER;
                        case "运行超时" -> Submission.STATUS_TIME_LIMIT_EXCEEDED;
                        case "内存超限" -> Submission.STATUS_MEMORY_LIMIT_EXCEEDED;
                        case "编译错误" -> Submission.STATUS_COMPILATION_ERROR;
                        default -> Submission.STATUS_RUNTIME_ERROR;
                    };
                    var language = submission.child(7).text();
                    var time = submission.child(8).text();
                    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    var timeInstant = LocalDateTime.parse(time, formatter).toInstant(ZoneOffset.ofHours(8));

                    var eSubmission = new Submission();
                    eSubmission.setOj(Submission.OJ_NOWCODER);
                    eSubmission.setUser(user);
                    eSubmission.setRemoteSubmissionId(remoteSubmissionId);
                    eSubmission.setName(problemName);
                    eSubmission.setRemoteProblemId(problemId);
                    eSubmission.setStatus(result);
                    eSubmission.setLanguage(language);
                    eSubmission.setSubmitTime(timeInstant);
                    return eSubmission;
                })
                .toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public boolean check(String user) {
        try (var httpclient = HttpClients.createDefault()) {
            var request = ClassicRequestBuilder.get("https://gw-c.nowcoder.com/api/sparta/user/profile/" + user)
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .build();
            return httpclient.execute(request, response -> {
                var mapper = new ObjectMapper();
                var res = mapper.readValue(response.getEntity().getContent(), NowcoderResponse.class);
                if (Boolean.TRUE.equals(res.success())) {
                    var data = mapper.convertValue(res.data(), NowcoderResponse.NowcoderAccountData.class);
                    return user.equals(data.id().toString());
                }
                return false;
            });
        } catch (IOException e) {
            return false;
        }
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
