package com.nytdacm.oa.model.entity;

import java.io.Serializable;

public class SocialAccount implements Serializable {
    private String codeforces;
    private Boolean codeforcesCrawlerEnabled;
    private Integer codeforcesRating;
    private Integer codeforcesMaxRating;
    private String codeforcesRank;

    public String getCodeforces() {
        return codeforces;
    }

    public void setCodeforces(String codeforces) {
        this.codeforces = codeforces;
    }

    public Boolean getCodeforcesCrawlerEnabled() {
        return codeforcesCrawlerEnabled;
    }

    public void setCodeforcesCrawlerEnabled(Boolean codeforcesCrawlerEnabled) {
        this.codeforcesCrawlerEnabled = codeforcesCrawlerEnabled;
    }

    public Integer getCodeforcesRating() {
        return codeforcesRating;
    }

    public void setCodeforcesRating(Integer codeforcesRating) {
        this.codeforcesRating = codeforcesRating;
    }

    public Integer getCodeforcesMaxRating() {
        return codeforcesMaxRating;
    }

    public void setCodeforcesMaxRating(Integer codeforcesMaxRating) {
        this.codeforcesMaxRating = codeforcesMaxRating;
    }

    public String getCodeforcesRank() {
        return codeforcesRank;
    }

    public void setCodeforcesRank(String codeforcesRank) {
        this.codeforcesRank = codeforcesRank;
    }

    private String github;

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    private String website;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
