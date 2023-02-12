package com.nytdacm.oa.third_part.crawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytdacm.oa.model.entity.Submission;
import com.nytdacm.oa.model.entity.User;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Component
public class AtCoderCrawler implements OJCrawler {
    @Override
    public List<Submission> crawl(String ojUser, long id, User user) {
        try (var httpclient = HttpClients.createDefault()) {
            var request = ClassicRequestBuilder
                .get("https://kenkoooo.com/atcoder/atcoder-api/results")
                .addParameter("user", ojUser)
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
        var mapper = new ObjectMapper();
        List<?> list;
        try {
            list = mapper.readValue((InputStream) data, List.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list.parallelStream()
            .map(o -> mapper.convertValue(o, AtCoderData.class))
            .sorted(Comparator.comparingLong(AtCoderData::id).reversed())
            .map(atCoderData -> {
                var submission = new Submission();
                submission.setUser(user);
                submission.setOj(Submission.OJ_ATCODER);
                submission.setSubmitTime(Instant.ofEpochSecond(atCoderData.epochSecond()));
                var status = switch (atCoderData.result()) {
                    case "AC" -> Submission.STATUS_SUCCESS;
                    case "WA" -> Submission.STATUS_WRONG_ANSWER;
                    case "CE" -> Submission.STATUS_COMPILATION_ERROR;
                    case "TLE" -> Submission.STATUS_TIME_LIMIT_EXCEEDED;
                    case "MLE" -> Submission.STATUS_MEMORY_LIMIT_EXCEEDED;
                    default -> Submission.STATUS_RUNTIME_ERROR;
                };
                submission.setStatus(status);
                submission.setContestId(atCoderData.contestId);
                submission.setRemoteSubmissionId(atCoderData.id.toString());
                submission.setLanguage(atCoderData.language());
                submission.setRemoteProblemId(atCoderData.problemId());
                return submission;
            })
            .filter(submission -> Long.parseLong(submission.getRemoteSubmissionId()) > id)
            .toList();
    }

    @Override
    public boolean check(String user) {
        try (var httpclient = HttpClients.createDefault()) {
            var request = ClassicRequestBuilder.get("https://atcoder.jp/users/" + user)
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .build();
            return httpclient.execute(request, response -> response.getCode() < 300);
        } catch (IOException e) {
            return false;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AtCoderData(
        Long id,
        @JsonProperty("epoch_second")
        Long epochSecond,
        @JsonProperty("problem_id")
        String problemId,
        @JsonProperty("contest_id")
        String contestId,
        @JsonProperty("user_id")
        String userId,
        String language,
        Double point,
        Long length,
        String result,
        @JsonProperty("execution_time")
        Long executionTime
    ) {
    }
}
