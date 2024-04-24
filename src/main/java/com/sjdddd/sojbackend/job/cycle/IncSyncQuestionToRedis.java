package com.sjdddd.sojbackend.job.cycle;

import com.sjdddd.sojbackend.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: 沈佳栋
 * @Description: 增量同步题目到 redis
 * @DateTime: 2024/4/24 下午8:48
 **/
@Component
@Slf4j
public class IncSyncQuestionToRedis {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RedisTemplate<String, Object> redisObjTemplate;

    // 每天凌晨1点执行
    @Scheduled(cron = "0 0 1 * * ?")
    public void run() {
        log.info("IncSyncQuestionToRedis start");
        questionService.list().forEach(question -> redisObjTemplate.opsForValue().set("question_" + question.getId(), question));
        log.info("IncSyncQuestionToRedis end");
    }

}
