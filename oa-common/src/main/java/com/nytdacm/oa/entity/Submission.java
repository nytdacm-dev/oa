package com.nytdacm.oa.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
public final class Submission extends BaseEntity {
    public static final String OJ_CODEFORCES = "codeforces";
    public static final String OJ_CODEFORCES_GYM = "codeforces_gym";
    public static final String OJ_NOWCODER = "nowcoder";
    public static final String OJ_ATCODER = "atcoder";
    public static final String OJ_VJUDGE = "vjudge";
    public static final String STATUS_SUCCESS = "OK";
    public static final String STATUS_WRONG_ANSWER = "WRONG_ANSWER";
    public static final String STATUS_TIME_LIMIT_EXCEEDED = "TIME_LIMIT_EXCEEDED";
    public static final String STATUS_MEMORY_LIMIT_EXCEEDED = "MEMORY_LIMIT_EXCEEDED";
    public static final String STATUS_COMPILATION_ERROR = "COMPILATION_ERROR";
    public static final String STATUS_RUNTIME_ERROR = "RUNTIME_ERROR";

    private Long submissionId;

    private User user;

    private String oj;

    private String remoteProblemId;

    private String name;

    private String remoteSubmissionId;

    private String contestId;

    private String language;

    private String status;

    private Instant submitTime;

    private Long relativeTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Submission that = (Submission) o;
        return Objects.equals(oj, that.oj) && Objects.equals(remoteSubmissionId, that.remoteSubmissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oj, remoteSubmissionId);
    }
}
