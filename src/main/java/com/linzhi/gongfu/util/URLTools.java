package com.linzhi.gongfu.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 用于操作URL的工具类
 *
 * @author xutao
 * @create_at 2021-12-29
 */
@Slf4j
public abstract class URLTools {
    /**
     * 从给定的主机名称中获取二级域名称
     *
     * @return 二级域名称
     * @throws IllegalArgumentException 提供主机为空或者不能从提供的主机名称中获取二级域名称时抛出
     */
    public static String extractSubdomainName(@NonNull String host) throws IllegalArgumentException {
        Assert.notNull(host, "所提供解析的主机名称不能为空。");
        log.debug("请求解析Host头：[{}]", host);
        return Arrays.stream(host.split("\\.")).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("无法从给定的主机名称中获取二级与名称。"));
    }

    /**
     * 判断给定的主机名称是否是本地域名，包括localhost与192.168开头的主机名称等
     *
     * @param host 给定的主机名称
     * @return 是否是本地域名
     * @throws IllegalArgumentException 提供的主机名称为空时抛出
     */
    public static boolean isRunningLocally(@NonNull String host) throws IllegalArgumentException {
        Assert.notNull(host, "所提供解析的主机名称不能为空。");
        return Stream.of("localhost", "127", "192").anyMatch(it -> host.toLowerCase().startsWith(it));
    }
}
