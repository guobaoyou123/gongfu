package com.linzhi.gongfu.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.linzhi.gongfu.entity.Session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
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

    /**
     * 为Jackson进行日期时间转换的时候增加新版Java Time支持。
     *
     * @return 增加了Java Time处理的对象映射处理器
     */
    @Bean
    public ObjectMapper getObjectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }
}
