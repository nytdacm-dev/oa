package com.nytdacm.oa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "t_users")
@Getter
@Setter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, updatable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "password_salt", nullable = false)
    private String passwordSalt;

    @Column(name = "name")
    private String name;

    @Column(name = "super_admin")
    private Boolean superAdmin = false;

    @Column(name = "admin")
    private Boolean admin = false;

    @Column(name = "active")
    private Boolean active = false;

    @Column(name = "social_account")
    @JdbcTypeCode(SqlTypes.JSON)
    private SocialAccount socialAccount = new SocialAccount();

    @ManyToMany(
        fetch = FetchType.EAGER
    )
    @JoinTable(
        name = "user_group",
        joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "user_id")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "group_id", referencedColumnName = "group_id")
        })
    private Set<Group> groups = new HashSet<>();

    private Instant lastActive;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    private Set<Submission> submissions = new HashSet<>();

    @Column(name = "user_internal")
    @JdbcTypeCode(SqlTypes.JSON)
    private UserInternal userInternal = new UserInternal();

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserInternal implements Serializable {
        private Boolean codeforcesCrawlerEnabled = false;
        private Long lastCodeforcesSubmissionId = 0L;
        private Boolean nowcoderCrawlerEnabled = false;
        private Long lastNowcoderSubmissionId = 0L;
        private Long lastAtCoderSubmissionId = 0L;
        private Boolean atcoderCrawlerEnabled = false;
        private Boolean vjudgeCrawlerEnabled = false;
        private Long lastVjudgeSubmissionId = 0L;
    }
}
