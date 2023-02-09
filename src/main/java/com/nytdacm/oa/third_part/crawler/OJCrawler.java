package com.nytdacm.oa.third_part.crawler;

import com.nytdacm.oa.model.entity.Submission;
import com.nytdacm.oa.model.entity.User;

import java.util.List;

public interface OJCrawler {
    List<Submission> crawl(String ojUser, long id, User user);

    List<Submission> parse(Object data, long id, User user);

    boolean check(String user);
}
