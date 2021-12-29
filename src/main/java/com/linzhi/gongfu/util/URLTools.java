package com.linzhi.gongfu.util;

import java.util.Arrays;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * 用于操作URL的工具类
 * @author xutao
 * @create_at 2021-12-29
 */
public abstract class URLTools {
    /**
     * 从给定的主机名称中获取二级域名称
     * @return 二级域名称
     * @throws IllegalArgumentException 提供主机为空或者不能从提供的主机名称中获取二级域名称时抛出
     */
    public static String extractSubdomainName(@NonNull String host) throws IllegalArgumentException {
        Assert.notNull(host, "所提供解析的主机名称不能为空。");
        return Arrays.stream(host.split(".")).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无法从给定的主机名称中获取二级与名称。"));
    }
}
