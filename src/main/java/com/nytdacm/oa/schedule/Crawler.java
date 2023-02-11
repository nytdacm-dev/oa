package com.nytdacm.oa.schedule;

import com.nytdacm.oa.dao.UserDao;
import com.nytdacm.oa.third_part.crawler.AtCoderCrawler;
import com.nytdacm.oa.third_part.crawler.PojCrawler;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class Crawler {
    private final UserDao userDao;
    private final AtCoderCrawler atCoderCrawler;
    private final PojCrawler pojCrawler;

    @Inject
    public Crawler(UserDao userDao, AtCoderCrawler atCoderCrawler, PojCrawler pojCrawler) {
        this.userDao = userDao;
        this.atCoderCrawler = atCoderCrawler;
        this.pojCrawler = pojCrawler;
    }

    @Scheduled(fixedDelay = 1800000) // 30分钟
    public void checkAtCoderAccount() {
        var users = userDao.findAll().stream()
            .filter(user -> user.getSocialAccount().getAtCoder() != null &&
                !Boolean.TRUE.equals(user.getUserInternal().getAtcoderCrawlerEnabled()))
            .peek(user -> {
                if (!atCoderCrawler.check(user.getSocialAccount().getAtCoder())) {
                    user.getSocialAccount().setAtCoder(null);
                    user.getUserInternal().setAtcoderCrawlerEnabled(false);
                } else {
                    user.getUserInternal().setAtcoderCrawlerEnabled(true);
                }
            })
            .toList();
        userDao.saveAll(users);
    }

    @Scheduled(cron = "0 0 3/6 * * *", zone = "Asia/Shanghai")
    public void crawlAtCoderSubmissions() {
        userDao.findAll().stream()
            .filter(user -> user.getSocialAccount().getAtCoder() != null &&
                Boolean.TRUE.equals(user.getUserInternal().getAtcoderCrawlerEnabled()))
            .forEach(user -> {
                var list = atCoderCrawler.crawl(user.getSocialAccount().getAtCoder(), user.getUserInternal().getLastAtCoderSubmissionId(), user);
                if (list.size() > 0) {
                    user.getUserInternal().setLastAtCoderSubmissionId(Long.parseLong(list.get(0).getRemoteSubmissionId()));
                    user.getSubmissions().addAll(list);
                    userDao.save(user);
                }
            });
    }

    @Scheduled(fixedDelay = 1800000) // 30分钟
    public void checkPojAccount() {
        var users = userDao.findAll().stream()
            .filter(user -> user.getSocialAccount().getPoj() != null &&
                !Boolean.TRUE.equals(user.getUserInternal().getPojCrawlerEnabled()))
            .peek(user -> {
                if (!pojCrawler.check(user.getSocialAccount().getPoj())) {
                    user.getSocialAccount().setPoj(null);
                    user.getUserInternal().setPojCrawlerEnabled(false);
                } else {
                    user.getUserInternal().setPojCrawlerEnabled(true);
                }
            })
            .toList();
        userDao.saveAll(users);
    }


    @Scheduled(cron = "0 30 2/24 * * *", zone = "Asia/Shanghai")
    public void crawlPojSubmissions() {
        userDao.findAll().stream()
            .filter(user -> user.getSocialAccount().getPoj() != null &&
                Boolean.TRUE.equals(user.getUserInternal().getPojCrawlerEnabled()))
            .forEach(user -> {
                var list = pojCrawler.crawl(user.getSocialAccount().getPoj(), user.getUserInternal().getLastPojSubmissionId(), user);
                if (list.size() > 0) {
                    user.getUserInternal().setLastPojSubmissionId(Long.parseLong(list.get(0).getRemoteSubmissionId()));
                    user.getSubmissions().addAll(list);
                    userDao.save(user);
                }
            });
    }
}
