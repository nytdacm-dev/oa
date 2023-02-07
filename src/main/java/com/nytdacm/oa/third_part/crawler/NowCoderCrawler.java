package com.nytdacm.oa.third_part.crawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytdacm.oa.dao.SubmissionDao;
import com.nytdacm.oa.dao.UserDao;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.StringUtils;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class NowCoderCrawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NowCoderCrawler.class);
    private final UserDao userDao;
    private final SubmissionDao submissionDao;

    @Inject
    public NowCoderCrawler(UserDao userDao, SubmissionDao submissionDao) {
        this.userDao = userDao;
        this.submissionDao = submissionDao;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60, zone = "Asia/Shanghai") // 1h
    public void nowcoderAccountCheckerCrawler() {
        LOGGER.info("开始验证用户牛客账号正确性并更新值");
        var users = userDao.findAll().stream()
            .filter(user -> StringUtils.isNotBlank(user.getSocialAccount().getNowcoder()) &&
                !Boolean.TRUE.equals(user.getSocialAccount().getNowcoderCrawlerEnabled()))
            .peek(user -> {
                var account = user.getSocialAccount().getNowcoder();
                try (var httpclient = HttpClients.createDefault()) {
                    var request = ClassicRequestBuilder.get("https://gw-c.nowcoder.com/api/sparta/user/profile/" + account)
                        .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .build();
                    httpclient.execute(request, response -> {
                        var mapper = new ObjectMapper();
                        var res = mapper.readValue(response.getEntity().getContent(), NowcoderResponse.class);
                        if (Boolean.TRUE.equals(res.success())) {
                            var data = mapper.convertValue(res.data(), NowcoderResponse.NowcoderAccountData.class);
                            if (account.equals(data.id().toString())) {
                                user.getSocialAccount().setNowcoderCrawlerEnabled(true);
                            } else {
                                user.getSocialAccount().setNowcoderCrawlerEnabled(false);
                                user.getSocialAccount().setNowcoder(null);
                                LOGGER.error(String.format("爬取 %s 用户的牛客账号（%s）时出错", user.getUsername(), account));
                            }
                        }
                        return null;
                    });
                } catch (Exception e) {
                    user.getSocialAccount().setNowcoderCrawlerEnabled(false);
                    user.getSocialAccount().setNowcoder(null);
                    LOGGER.error(String.format("爬取 %s 用户的牛客账号（%s）时出错", user.getUsername(), account), e);
                }
            })
            .toList();
        userDao.saveAll(users);
        LOGGER.info("牛客账号验证成功，本次共验证了 " + users.size() + " 个账号");
    }
}

record NowcoderResponse<T>(
    Boolean success,
    Integer code,
    String msg,
    T data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NowcoderAccountData(
        Integer id,
        String nickname,
        String introduction,
        Integer workType,
        Integer workStatusDetail,
        String workTime,
        String eduLevel,
        String educationInfo
    ) {
    }
}
