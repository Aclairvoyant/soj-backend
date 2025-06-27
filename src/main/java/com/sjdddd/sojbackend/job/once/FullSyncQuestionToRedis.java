package com.sjdddd.sojbackend.job.once;

import com.sjdddd.sojbackend.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: 沈佳栋
 * @Description: 全量同步题目到 redis
 * @DateTime: 2024/4/24 下午8:51
 **/
@Component
@Slf4j
public class FullSyncQuestionToRedis implements CommandLineRunner {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RedisTemplate<String, Object> redisObjTemplate;

    @Autowired
    private CacheManager cacheManager;


    @Override
    public void run(String... args) throws Exception {
        log.info("FullSyncQuestionToRedis start");
        //questionService.list().forEach(question -> redisObjTemplate.opsForValue().set("question_" + question.getId(), question));
        // 拿到名为 "question" 的缓存区
        Cache questionCache = cacheManager.getCache("question");
        if (questionCache == null) {
            log.warn("No cache named 'question' found!");
            return;
        }
        // 全量同步：把每个 question 依次放到 Spring Cache 管理的 Redis 里
        questionService.list().forEach(q -> {
            questionCache.put(q.getId(), q);
        });
        log.info("FullSyncQuestionToRedis end");
    }
}
