package com.sjdddd.sojbackend.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 沈佳栋
 * @Description: 推荐服务队列
 * @DateTime: 2025/3/26 13:53
 **/
@Configuration
public class RecommendConfig {
    @Bean
    public TopicExchange recommendExchange() {
        return new TopicExchange("recommendExchange");
    }

    @Bean
    public Queue recommendAnalysisQueue() {
        return new Queue("recommendAnalysisQueue");
    }

    @Bean
    public Binding recommendBinding() {
        return BindingBuilder.bind(recommendAnalysisQueue())
                .to(recommendExchange())
                .with("analysis.task");
    }
}
