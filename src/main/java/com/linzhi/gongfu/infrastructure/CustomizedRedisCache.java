package com.linzhi.gongfu.infrastructure;


import lombok.NonNull;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

/**
 * 自定义RedisCache，用于模糊删除缓存数据
 *
 */
public class CustomizedRedisCache extends RedisCache {
    private static final String WILD_CARD = "*";

    private final String name;
    private final RedisCacheWriter cacheWriter;
    private final ConversionService conversionService;

    public CustomizedRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
        this.name = name;
        this.cacheWriter = cacheWriter;
        this.conversionService = cacheConfig.getConversionService();
    }

    @Override
    public void evict(@NonNull Object key) {
        if (key instanceof String) {
            String keyString = key.toString();
            if (keyString.endsWith(WILD_CARD)) {
                evictLikeSuffix(keyString);
                return;
            }
            if (keyString.startsWith(WILD_CARD)) {
                evictLikePrefix(keyString);
                return;
            }
        }
        super.evict(key);
    }

    /**
     * 前缀匹配
     *
     * @param key key值
     */
    public void evictLikePrefix(String key) {
        byte[] pattern = this.conversionService.convert(this.createCacheKey(key), byte[].class);
        this.cacheWriter.clean(this.name, pattern);
    }

    /**
     * 后缀匹配
     *
     * @param key key值
     */
    public void evictLikeSuffix(String key) {
        byte[] pattern = this.conversionService.convert(this.createCacheKey(key), byte[].class);
        this.cacheWriter.clean(this.name, pattern);
    }


}
