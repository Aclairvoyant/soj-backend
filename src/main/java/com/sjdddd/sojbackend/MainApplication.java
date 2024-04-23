package com.sjdddd.sojbackend;

import com.sjdddd.sojbackend.mq.CodeMqInitMain;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 */
@SpringBootApplication()
@MapperScan("com.sjdddd.sojbackend.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Slf4j
public class MainApplication {

    public static void main(String[] args) {
        log.info("消息队列启动中...");
        CodeMqInitMain.doInitCodeMq();
        log.info("项目启动中...");
        SpringApplication.run(MainApplication.class, args);
    }

}
