package com.nytdacm.oa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "t_submissions")
@Getter
@Setter
public class Submission extends BaseEntity {
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id", nullable = false, updatable = false)
    private Long submissionId;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Column(name = "oj", nullable = false)
    private String oj;

    @Column(name = "remote_problem_id")
    private String remoteProblemId;

    @Column(name = "name")
    private String name;

    @Column(name = "remote_submission_id")
    private String remoteSubmissionId;

    @Column(name = "contest_id")
    private String contestId;

    @Column(name = "language")
    private String language;

    @Column(name = "status")
    private String status;

    @Column(name = "submit_time")
    private Instant submitTime;

    @Column(name = "relative_time")
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
