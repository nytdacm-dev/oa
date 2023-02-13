package com.nytdacm.oa.schedule;

import com.nytdacm.oa.dao.UserDao;
import com.nytdacm.oa.third_part.crawler.AtCoderCrawler;
import com.nytdacm.oa.third_part.crawler.PojCrawler;
import com.nytdacm.oa.third_part.crawler.VjudgeCrawler;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class Crawler {
    private final UserDao userDao;
    private final AtCoderCrawler atCoderCrawler;
    private final PojCrawler pojCrawler;
    private final VjudgeCrawler vjudgeCrawler;

    public Crawler(UserDao userDao, AtCoderCrawler atCoderCrawler, PojCrawler pojCrawler, VjudgeCrawler vjudgeCrawler) {
        this.userDao = userDao;
        this.atCoderCrawler = atCoderCrawler;
        this.pojCrawler = pojCrawler;
        this.vjudgeCrawler = vjudgeCrawler;
    }

    @Scheduled(fixedDelay = 1800000) // 30分钟
    public void checkAtCoderAccount() {
        var users = userDao.findAll().parallelStream()
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
        userDao.findAll().parallelStream()
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
        var users = userDao.findAll().parallelStream()
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
        userDao.findAll().parallelStream()
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

    @Scheduled(fixedDelay = 1800000) // 30分钟
    public void checkVjudgeAccount() {
        var users = userDao.findAll().parallelStream()
            .filter(user -> user.getSocialAccount().getVjudge() != null &&
                !Boolean.TRUE.equals(user.getUserInternal().getVjudgeCrawlerEnabled()))
            .peek(user -> {
                if (!vjudgeCrawler.check(user.getSocialAccount().getVjudge())) {
                    user.getSocialAccount().setVjudge(null);
                    user.getUserInternal().setVjudgeCrawlerEnabled(false);
                } else {
                    user.getUserInternal().setVjudgeCrawlerEnabled(true);
                }
            })
            .toList();
        userDao.saveAll(users);
    }

    @Scheduled(cron = "35 30 1/24 * * *", zone = "Asia/Shanghai")
    public void crawlVjudgeSubmissions() {
        userDao.findAll().parallelStream()
            .filter(user -> user.getSocialAccount().getVjudge() != null &&
                Boolean.TRUE.equals(user.getUserInternal().getVjudgeCrawlerEnabled()))
            .forEach(user -> {
                var list = vjudgeCrawler.crawl(user.getSocialAccount().getVjudge(), user.getUserInternal().getLastVjudgeSubmissionId(), user);
                if (list.size() > 0) {
                    user.getUserInternal().setLastVjudgeSubmissionId(Long.parseLong(list.get(0).getRemoteSubmissionId()));
                    user.getSubmissions().addAll(list);
                    userDao.save(user);
                }
            });
    }
}
