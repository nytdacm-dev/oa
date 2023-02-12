package com.nytdacm.oa.third_part.crawler;

import com.nytdacm.oa.model.entity.Submission;
import com.nytdacm.oa.model.entity.User;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PojCrawler implements OJCrawler {
    @Override
    public List<Submission> crawl(String ojUser, long id, User user) {
        try (var httpclient = HttpClients.createDefault()) {
            var request = ClassicRequestBuilder
                .get("http://poj.org/status")
                .addParameter("user_id", ojUser)
                .addParameter("size", "200")
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
        Document document;
        try {
            document = Jsoup.parse((InputStream) data, "UTF-8", "http://poj.org/status");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var elements = document.select("body > table.a > tbody > tr").parallelStream().filter(element -> !"in".equals(element.className())).toList();
        return elements.parallelStream().map(element -> {
            var submission = new Submission();
            submission.setOj(Submission.OJ_POJ);
            submission.setUser(user);
            submission.setRemoteSubmissionId(element.child(0).text());
            submission.setRemoteProblemId(element.child(2).text());
            var status = switch (element.child(3).text()) {
                case "Accepted" -> Submission.STATUS_SUCCESS;
                case "Wrong Answer" -> Submission.STATUS_WRONG_ANSWER;
                case "Compile Error" -> Submission.STATUS_COMPILATION_ERROR;
                case "Time Limit Exceeded" -> Submission.STATUS_TIME_LIMIT_EXCEEDED;
                case "Memory Limit Exceeded" -> Submission.STATUS_MEMORY_LIMIT_EXCEEDED;
                default -> Submission.STATUS_RUNTIME_ERROR;
            };
            submission.setStatus(status);
            submission.setLanguage(element.child(6).text());
            var time = element.child(8).text();
            var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            submission.setSubmitTime(LocalDateTime.parse(time, formatter).toInstant(ZoneOffset.ofHours(8)));
            return submission;
        }).toList();
    }

    @Override
    public boolean check(String user) {
        try (var httpclient = HttpClients.createDefault()) {
            var request = ClassicRequestBuilder.get("http://poj.org/userstatus")
                .addParameter("user_id", user)
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .build();
            return httpclient.execute(request, response -> {
                var status = response.getCode();
                if (status >= 300 || status < 200) {
                    return false;
                }
                var content = response.getEntity().getContent();
                var str = new String(content.readAllBytes());
                return !str.contains("Error");
            });
        } catch (IOException e) {
            return false;
        }
    }
}
