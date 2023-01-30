package com.nytdacm.oa.model.entity;

import java.io.Serializable;

public class SocialAccount implements Serializable {
    private String codeforces;
    private int codeforcesRating;
    private int codeforcesMaxRating;
    private String codeforcesRank;

    public String getCodeforces() {
        return codeforces;
    }

    public void setCodeforces(String codeforces) {
        this.codeforces = codeforces;
    }

    public int getCodeforcesRating() {
        return codeforcesRating;
    }

    public void setCodeforcesRating(int codeforcesRating) {
        this.codeforcesRating = codeforcesRating;
    }

    public int getCodeforcesMaxRating() {
        return codeforcesMaxRating;
    }

    public void setCodeforcesMaxRating(int codeforcesMaxRating) {
        this.codeforcesMaxRating = codeforcesMaxRating;
    }

    public String getCodeforcesRank() {
        return codeforcesRank;
    }

    public void setCodeforcesRank(String codeforcesRank) {
        this.codeforcesRank = codeforcesRank;
    }
}