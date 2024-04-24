package com.sjdddd.sojbackend.job.once;

import com.sjdddd.sojbackend.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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


    @Override
    public void run(String... args) throws Exception {
        log.info("FullSyncQuestionToRedis start");
        questionService.list().forEach(question -> redisObjTemplate.opsForValue().set("question_" + question.getId(), question));
        log.info("FullSyncQuestionToRedis end");
    }
}
