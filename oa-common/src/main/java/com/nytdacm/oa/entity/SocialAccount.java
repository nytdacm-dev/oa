package com.nytdacm.oa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SocialAccount implements Serializable {
    private String codeforces;
    private Integer codeforcesRating;
    private Integer codeforcesMaxRating;
    private String codeforcesRank;

    private String atCoder;

    private String luogu;

    private String nowcoder;
    private String vjudge;
}
