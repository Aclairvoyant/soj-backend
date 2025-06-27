package com.sjdddd.sojbackend.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * redis 序列化配置
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    private static final String STANDARD_PATTERN = "yyyy/MM/dd HH:mm:ss";
    private static final String DATE_PATTERN = "yyyy/MM/dd";
    private static final String TIME_PATTERN = "HH:mm:ss";

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        // 使用Jackson2JsonRedisSerialize 替换默认序列化
//        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
//                new Jackson2JsonRedisSerializer<>(Object.class);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                JsonTypeInfo.As.PROPERTY);
//        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        this.registerLocalDateTime(objectMapper);
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//
//        template.setConnectionFactory(connectionFactory);
//        // key采用String的序列化方式
//        template.setKeySerializer(new StringRedisSerializer());
//        // value序列化方式采用jackson
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//        // hash的key也采用String的序列化方式
//        template.setHashKeySerializer(new StringRedisSerializer());
//        // hash的value序列化方式采用jackson
//        template.setHashValueSerializer(jackson2JsonRedisSerializer);
//        template.afterPropertiesSet();
//        return template;
//    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisObjTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        // 添加自定义的模块处理Java8新日期API
        mapper.registerModule(new JavaTimeModule());
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);

        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 处理时间类型
     *
     * @param objectMapper
     */
    private void registerLocalDateTime(ObjectMapper objectMapper) {
        // 设置java.util.Date时间类的序列化以及反序列化的格式
        objectMapper.setDateFormat(new SimpleDateFormat(STANDARD_PATTERN));

        JavaTimeModule timeModule = new JavaTimeModule();
        // LocalDateTime
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(STANDARD_PATTERN);
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        // LocalDate
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        timeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        // LocalTime
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
        timeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        objectMapper.registerModule(timeModule);
    }

    /**
     * 定制 Spring Cache 在 Redis 中的序列化方式：
     * key 用 String，value 用 Jackson JSON 序列化
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        // JSON 序列化器，能够自动带上类型信息，反序列化时回到原类
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer();

        return RedisCacheConfiguration.defaultCacheConfig()
                // key 和 hash key 都用字符串
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                // value 和 hash value 都用 JSON
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer))
                // 可选：全局缓存过期时间，12 小时
                .entryTtl(Duration.ofHours(12));
    }

    /**
     * 使用上面那个 redisCacheConfiguration 来构建 CacheManager，
     * 让所有 @Cacheable / @CacheEvict 等注解都走这个 JSON 化的配置。
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                // 把默认 cacheNames 都应用上面定义的序列化配置
                .cacheDefaults(redisCacheConfiguration())
                .transactionAware()
                .build();
    }
}
