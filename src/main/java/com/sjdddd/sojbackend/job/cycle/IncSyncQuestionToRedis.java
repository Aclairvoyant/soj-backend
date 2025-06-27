package com.sjdddd.sojbackend.job.cycle;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sjdddd.sojbackend.model.entity.Question;
import com.sjdddd.sojbackend.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

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
    private CacheManager cacheManager;  // Spring Cache 的统一管理器

    // 上次同步时间，第一次启动时向前推一天
    private volatile LocalDateTime lastSyncTime = LocalDateTime.now().minusDays(1);

    /**
     * 每天凌晨1点执行：只把自上次同步后新增或更新过的题目，写入 Spring Cache 管理的 Redis。
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void run() {
        log.info("IncSyncQuestionToRedis start, lastSyncTime={}", lastSyncTime);
        Cache questionCache = cacheManager.getCache("question");
        if (questionCache == null) {
            log.warn("缓存区 'question' 未找到，跳过增量同步");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<Question> updatedList = questionService.list(
                Wrappers.<Question>lambdaQuery()
                        .ge(Question::getUpdateTime, lastSyncTime)
        );

        updatedList.forEach(q -> {
            // key = question::[id]
            questionCache.put(q.getId(), q);
        });

        log.info("IncSyncQuestionToRedis end, synced {} items", updatedList.size());
        // 更新 lastSyncTime
        lastSyncTime = now;
    }

}
