package com.linzhi.gongfu.infrastructure;

import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linzhi.gongfu.entity.Session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 用于存放基础设施Bean的工具类
 *
 * @author xutao
 * @create_at 2021-12-22
 */
@Configuration
public class BeanFactory {
    /**
     * 用于在Redis中操作用户会话信息的
     *
     * @param connectionFactory Redis连接工厂
     * @param objectMapper      对象序列化器
     * @return 用于操作Redis的RedisTemplate对象
     */
    @Bean(name = "SessionTemplate")
    public RedisTemplate<String, Session> tokenRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {
        RedisTemplate<String, Session> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

        var jsonSerializer = new Jackson2JsonRedisSerializer<>(Session.class);
        jsonSerializer.setObjectMapper(objectMapper);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        return template;
    }

    @Bean
    public RedisCacheConfiguration getRedisCacheConfiguration(
            @Value("${spring.cache.redis.time-to-live}") Integer seconds,
            SerializationPair<Object> serializationPair) {

        var configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration
                .serializeValuesWith(serializationPair)
                .entryTtl(Duration.ofSeconds(seconds));

        return configuration;
    }

    @Primary
    @Bean
    public RedisCacheManager ttlCacheManager(
            RedisConnectionFactory redisConnectionFactory,
            RedisCacheConfiguration redisCacheConfiguration) {
        return new CustomTtlRedisCacheManager(
                RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory),
                redisCacheConfiguration);
    }
}
