package com.nytdacm.oa.crawler.schedule;

import com.nytdacm.oa.dao.UserDao;
import com.nytdacm.oa.crawler.AtCoderCrawler;
import com.nytdacm.oa.crawler.CodeforcesCrawler;
import com.nytdacm.oa.crawler.NowCoderCrawler;
import com.nytdacm.oa.crawler.VjudgeCrawler;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class Crawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);
    private final UserDao userDao;
    private final AtCoderCrawler atCoderCrawler;
    private final VjudgeCrawler vjudgeCrawler;
    private final NowCoderCrawler nowCoderCrawler;
    private final CodeforcesCrawler codeforcesCrawler;

    public Crawler(UserDao userDao, AtCoderCrawler atCoderCrawler, VjudgeCrawler vjudgeCrawler, NowCoderCrawler nowCoderCrawler, CodeforcesCrawler codeforcesCrawler) {
        this.userDao = userDao;
        this.atCoderCrawler = atCoderCrawler;
        this.vjudgeCrawler = vjudgeCrawler;
        this.nowCoderCrawler = nowCoderCrawler;
        this.codeforcesCrawler = codeforcesCrawler;
    }

    @Scheduled(fixedDelay = 1873000) // 30分钟左右
    public void checkCodeforcesAccount() {
        LOGGER.debug("开始检查 Codeforces 账号");
        var users = userDao.findAll().parallelStream()
            .filter(user -> user.getSocialAccount().getCodeforces() != null &&
                !Boolean.TRUE.equals(user.getUserInternal().getCodeforcesCrawlerEnabled()))
            .peek(user -> {
                LOGGER.debug("开始检查 {} 的 Codeforces 账号，账号为 {}", user.getName(), user.getSocialAccount().getCodeforces());
                if (!codeforcesCrawler.check(user.getSocialAccount().getCodeforces())) {
                    LOGGER.debug("检查 {} 的 Codeforces 账号失败，填写的账号为 {}", user.getName(), user.getSocialAccount().getCodeforces());
                    user.getSocialAccount().setCodeforces(null);
                    user.getUserInternal().setCodeforcesCrawlerEnabled(false);
                } else {
                    LOGGER.debug("检查 {} 的 Codeforces 账号成功", user.getName());
                    user.getUserInternal().setCodeforcesCrawlerEnabled(true);
                }
            })
            .toList();
        userDao.saveAll(users);
        LOGGER.debug("检查 Codeforces 账号结束，共检查 {} 个账号", users.size());
    }

    @Scheduled(fixedDelay = 1111 * 58 * 62 * 3, zone = "Asia/Shanghai") // 3h 左右
    public void crawlCodeforcesSubmissions() {
        LOGGER.debug("开始爬取 Codeforces 提交记录");
        userDao.findAll().parallelStream()
            .filter(user -> user.getSocialAccount().getCodeforces() != null &&
                Boolean.TRUE.equals(user.getUserInternal().getCodeforcesCrawlerEnabled()))
            .forEach(user -> {
                LOGGER.debug("开始爬取 {} 的 Codeforces 提交记录，账号为 {}", user.getName(), user.getSocialAccount().getCodeforces());
                var list = codeforcesCrawler.crawl(user.getSocialAccount().getCodeforces(), user.getUserInternal().getLastCodeforcesSubmissionId(), user);
                if (list.size() > 0) {
                    LOGGER.debug("爬取 {} 的 Codeforces 提交记录成功，共爬取 {} 条记录", user.getName(), list.size());
                    user.getUserInternal().setLastCodeforcesSubmissionId(Long.parseLong(list.get(0).getRemoteSubmissionId()));
                    user.getSubmissions().addAll(list);
                    userDao.save(user);
                }
                LOGGER.debug("{} 的 Codeforces 提交记录爬取结束", user.getName());
            });
        LOGGER.debug("爬取 Codeforces 提交记录结束");
    }

    @Scheduled(fixedDelay = 1853000) // 30分钟左右
    public void checkAtCoderAccount() {
        LOGGER.debug("开始检查 AtCoder 账号");
        var users = userDao.findAll().parallelStream()
            .filter(user -> user.getSocialAccount().getAtCoder() != null &&
                !Boolean.TRUE.equals(user.getUserInternal().getAtcoderCrawlerEnabled()))
            .peek(user -> {
                LOGGER.debug("开始检查 {} 的 AtCoder 账号，账号为 {}", user.getName(), user.getSocialAccount().getAtCoder());
                if (!atCoderCrawler.check(user.getSocialAccount().getAtCoder())) {
                    LOGGER.debug("检查 {} 的 AtCoder 账号失败，填写的账号为 {}", user.getName(), user.getSocialAccount().getAtCoder());
                    user.getSocialAccount().setAtCoder(null);
                    user.getUserInternal().setAtcoderCrawlerEnabled(false);
                } else {
                    LOGGER.debug("检查 {} 的 AtCoder 账号成功", user.getName());
                    user.getUserInternal().setAtcoderCrawlerEnabled(true);
                }
            })
            .toList();
        userDao.saveAll(users);
        LOGGER.debug("检查 AtCoder 账号结束，共检查 {} 个账号", users.size());
    }

    @Scheduled(fixedDelay = 1111 * 58 * 62 * 6, zone = "Asia/Shanghai") // 6h 左右
    public void crawlAtCoderSubmissions() {
        LOGGER.debug("开始爬取 AtCoder 提交记录");
        userDao.findAll().parallelStream()
            .filter(user -> user.getSocialAccount().getAtCoder() != null &&
                Boolean.TRUE.equals(user.getUserInternal().getAtcoderCrawlerEnabled()))
            .forEach(user -> {
                LOGGER.debug("开始爬取 {} 的 AtCoder 提交记录，账号为 {}", user.getName(), user.getSocialAccount().getAtCoder());
                var list = atCoderCrawler.crawl(user.getSocialAccount().getAtCoder(), user.getUserInternal().getLastAtCoderSubmissionId(), user);
                if (list.size() > 0) {
                    LOGGER.debug("爬取 {} 的 AtCoder 提交记录成功，共爬取 {} 条记录", user.getName(), list.size());
                    user.getUserInternal().setLastAtCoderSubmissionId(Long.parseLong(list.get(0).getRemoteSubmissionId()));
                    user.getSubmissions().addAll(list);
                    userDao.save(user);
                }
                LOGGER.debug("{} 的 AtCoder 提交记录爬取结束", user.getName());
            });
        LOGGER.debug("爬取 AtCoder 提交记录结束");
    }

    @Scheduled(fixedDelay = 1100 * 59 * 31) // 30 分钟左右
    public void checkVjudgeAccount() {
        LOGGER.debug("开始检查 Vjudge 账号");
        var users = userDao.findAll().parallelStream()
            .filter(user -> user.getSocialAccount().getVjudge() != null &&
                !Boolean.TRUE.equals(user.getUserInternal().getVjudgeCrawlerEnabled()))
            .peek(user -> {
                LOGGER.debug("开始检查 {} 的 Vjudge 账号，账号为 {}", user.getName(), user.getSocialAccount().getVjudge());
                if (!vjudgeCrawler.check(user.getSocialAccount().getVjudge())) {
                    LOGGER.debug("检查 {} 的 Vjudge 账号失败，填写的账号为 {}", user.getName(), user.getSocialAccount().getVjudge());
                    user.getSocialAccount().setVjudge(null);
                    user.getUserInternal().setVjudgeCrawlerEnabled(false);
                } else {
                    LOGGER.debug("检查 {} 的 Vjudge 账号成功", user.getName());
                    user.getUserInternal().setVjudgeCrawlerEnabled(true);
                }
            })
            .toList();
        userDao.saveAll(users);
        LOGGER.debug("检查 Vjudge 账号结束，共检查 {} 个账号", users.size());
    }

    @Scheduled(fixedDelay = 1000 * 60 * 61, zone = "Asia/Shanghai") // 1h 左右
    public void crawlVjudgeSubmissions() {
        LOGGER.debug("开始爬取 Vjudge 提交记录");
        userDao.findAll().parallelStream()
            .filter(user -> user.getSocialAccount().getVjudge() != null &&
                Boolean.TRUE.equals(user.getUserInternal().getVjudgeCrawlerEnabled()))
            .forEach(user -> {
                LOGGER.debug("开始爬取 {} 的 Vjudge 提交记录，账号为 {}", user.getName(), user.getSocialAccount().getVjudge());
                var list = vjudgeCrawler.crawl(user.getSocialAccount().getVjudge(), user.getUserInternal().getLastVjudgeSubmissionId(), user);
                if (list.size() > 0) {
                    LOGGER.debug("爬取 {} 的 Vjudge 提交记录成功，共爬取 {} 条记录", user.getName(), list.size());
                    user.getUserInternal().setLastVjudgeSubmissionId(Long.parseLong(list.get(0).getRemoteSubmissionId()));
                    user.getSubmissions().addAll(list);
                    userDao.save(user);
                }
                LOGGER.debug("{} 的 Vjudge 提交记录爬取结束", user.getName());
            });
        LOGGER.debug("爬取 Vjudge 提交记录结束");
    }

    @Scheduled(fixedDelay = 1000 * 60 * 61, zone = "Asia/Shanghai") // 1h 左右
    public void checkNowcoderAccount() {
        LOGGER.debug("开始检查 Nowcoder 账号");
        var users = userDao.findAll().parallelStream()
            .filter(user -> user.getSocialAccount().getNowcoder() != null &&
                !Boolean.TRUE.equals(user.getUserInternal().getNowcoderCrawlerEnabled()))
            .peek(user -> {
                LOGGER.debug("开始检查 {} 的 Nowcoder 账号，账号为 {}", user.getName(), user.getSocialAccount().getNowcoder());
                if (!nowCoderCrawler.check(user.getSocialAccount().getNowcoder())) {
                    LOGGER.debug("检查 {} 的 Nowcoder 账号失败，填写的账号为 {}", user.getName(), user.getSocialAccount().getNowcoder());
                    user.getSocialAccount().setNowcoder(null);
                    user.getUserInternal().setNowcoderCrawlerEnabled(false);
                } else {
                    LOGGER.debug("检查 {} 的 Nowcoder 账号成功", user.getName());
                    user.getUserInternal().setNowcoderCrawlerEnabled(true);
                }
            })
            .toList();
        userDao.saveAll(users);
        LOGGER.debug("检查 Nowcoder 账号结束，共检查 {} 个账号", users.size());
    }

    @Scheduled(fixedDelay = 1000 * 60 * 62 * 2, zone = "Asia/Shanghai") // 2h左右
    public void crawlNowcoderSubmissions() {
        LOGGER.debug("开始爬取牛客提交记录");
        userDao.findAll().parallelStream()
            .filter(user -> user.getSocialAccount().getNowcoder() != null &&
                Boolean.TRUE.equals(user.getUserInternal().getNowcoderCrawlerEnabled()))
            .forEach(user -> {
                LOGGER.debug("爬取 {} 的牛客提交记录，牛客账号为 {}", user.getName(), user.getSocialAccount().getNowcoder());
                var list = nowCoderCrawler.crawl(user.getSocialAccount().getNowcoder(), user.getUserInternal().getLastNowcoderSubmissionId(), user);
                if (list.size() > 0) {
                    LOGGER.debug("{} 的牛客账号爬取到 {} 条提交记录", user.getName(), list.size());
                    user.getUserInternal().setLastNowcoderSubmissionId(Long.parseLong(list.get(0).getRemoteSubmissionId()));
                    user.getSubmissions().addAll(list);
                    userDao.save(user);
                }
                LOGGER.debug("{} 的牛客提交记录爬取结束", user.getName());
            });
        LOGGER.debug("爬取牛客提交记录结束");
    }
}
