package com.linzhi.gongfu.infrastructure;

import java.time.Duration;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

/**
 * 自定义用于支持在设定方法缓存的时候重新设置缓存时间的缓存管理器。
 *
 * 缓存时间的设置使用{@code @Cacheable}注解中的{@code value}属性设置，该属性值中使用分号分割，分别设置不同的内容。
 * 其中分割开的第一项内容为换准的名称，第二项内容为缓存的存活时间，单位为<em>秒</em>。
 *
 * @author xutao
 * @create_at 2022-01-20
 */
public class CustomTtlRedisCacheManager extends RedisCacheManager {
    public CustomTtlRedisCacheManager(RedisCacheWriter writer, RedisCacheConfiguration configuration) {
        super(writer, configuration);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        String[] units = name.split(";");
        if (units.length > 1) {
            long ttl = Long.parseLong(units[1]);
            cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(ttl));
        }
        return super.createRedisCache(units[0], cacheConfig);
    }

}
