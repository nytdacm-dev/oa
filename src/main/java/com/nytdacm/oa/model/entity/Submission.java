package com.nytdacm.oa.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "t_submissions")
@SQLDelete(sql = "UPDATE t_submissions SET deleted = true WHERE submission_id = ?")
@FilterDef(name = "deletedProductFilter", parameters = @ParamDef(name = "deleted", type = boolean.class))
@Filter(name = "deletedProductFilter", condition = "deleted = :deleted")
public class Submission extends BaseEntity {
    public static final String OJ_CODEFORCES = "codeforces";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_WRONG_ANSWER = "WRONG_ANSWER";
    public static final String STATUS_TIME_LIMIT_EXCEEDED = "TIME_LIMIT_EXCEEDED";
    public static final String STATUS_MEMORY_LIMIT_EXCEEDED = "MEMORY_LIMIT_EXCEEDED";
    public static final String STATUS_RUNTIME_ERROR = "RUNTIME_ERROR";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id", nullable = false, updatable = false)
    private Long submissionId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
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

    public Long getSubmissionId() {
        return submissionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOj() {
        return oj;
    }

    public void setOj(String oj) {
        this.oj = oj;
    }

    public String getRemoteProblemId() {
        return remoteProblemId;
    }

    public void setRemoteProblemId(String remoteProblemId) {
        this.remoteProblemId = remoteProblemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemoteSubmissionId() {
        return remoteSubmissionId;
    }

    public void setRemoteSubmissionId(String remoteSubmissionId) {
        this.remoteSubmissionId = remoteSubmissionId;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Instant submitTime) {
        this.submitTime = submitTime;
    }

    public Long getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(Long relativeTime) {
        this.relativeTime = relativeTime;
    }

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
