package com.nytdacm.oa.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytdacm.oa.entity.Submission;
import com.nytdacm.oa.entity.User;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class VjudgeCrawler implements OJCrawler {
    @Override
    public List<Submission> crawl(String ojUser, long id, User user) {
        List<Submission> submissions = new ArrayList<>();
        try (var httpclient = HttpClients.createDefault()) {
            for (int i = 0; i < 10; ++i) {
                var request = ClassicRequestBuilder
                    .get("https://vjudge.net/status/data")
                    .addParameter("draw", "1")
                    .addParameter("start", String.valueOf(20 * i))
                    .addParameter("length", "20")
                    .addParameter("un", ojUser)
                    .addParameter("OJId", "All")
                    .addParameter("probNum", "")
                    .addParameter("res", "0")
                    .addParameter("language", "")
                    .addParameter("onlyFollowee", "false")
                    .addParameter("_", String.valueOf(System.currentTimeMillis()))
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .build();
                var result = httpclient.execute(request, response -> {
                    var data = response.getEntity().getContent();
                    return parse(data, id, user);
                });
                submissions.addAll(result);
                if (result.size() < 20) break;
            }
            submissions.sort(Comparator.comparing(Submission::getRemoteSubmissionId).reversed());
            return submissions;
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public List<Submission> parse(Object data, long id, User user) {
        try {
            var mapper = new ObjectMapper();
            var response = mapper.readValue((InputStream) data, VjResponse.class);
            return response.data.parallelStream()
                .filter(record -> record.runId() > id)
                .filter(record -> record.statusType < 2)
                .map(record -> {
                    var submission = new Submission();
                    submission.setUser(user);
                    submission.setOj(Submission.OJ_VJUDGE);
                    submission.setSubmitTime(Instant.ofEpochMilli(record.time()));
                    submission.setRemoteSubmissionId(String.valueOf(record.runId()));
                    String status;
                    if (record.statusType() == 0) {
                        status = Submission.STATUS_SUCCESS;
                    } else {
                        if (record.status().toLowerCase().contains("wrong answer")) {
                            status = Submission.STATUS_WRONG_ANSWER;
                        } else if (record.status().toLowerCase().contains("time")) {
                            status = Submission.STATUS_TIME_LIMIT_EXCEEDED;
                        } else if (record.status().toLowerCase().contains("memory")) {
                            status = Submission.STATUS_MEMORY_LIMIT_EXCEEDED;
                        } else if (record.status().toLowerCase().contains("compile")) {
                            status = Submission.STATUS_COMPILATION_ERROR;
                        } else {
                            status = Submission.STATUS_RUNTIME_ERROR;
                        }
                    }
                    submission.setStatus(status);
                    submission.setLanguage(record.language());
                    submission.setRemoteProblemId(record.oj() + "-" + record.probNum());
                    return submission;
                }).toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public boolean check(String user) {
        try (var httpclient = HttpClients.createDefault()) {
            var request = ClassicRequestBuilder.get("https://vjudge.net/user/" + user)
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .build();
            return httpclient.execute(request, response -> {
                var status = response.getCode();
                return status < 300 && status >= 200;
            });
        } catch (IOException e) {
            return false;
        }
    }

    private record VjResponse(
        List<VjRecord> data,
        Long recordsTotal,
        Long recordsFiltered,
        Long draw
    ) {
        private record VjRecord(
            Long memory,
            Long access,
            Long statusType,
            String avatarUrl,
            Long runtime,
            String language,
            String userName,
            Long userId,
            String languageCanonical,
            Boolean processing,
            Long runId,
            Long time,
            String oj,
            Long problemId,
            Long sourceLength,
            String probNum,
            String status,
            Long contestOpenness,
            Long contestId,
            String contestNum
        ) {
        }
    }
}
