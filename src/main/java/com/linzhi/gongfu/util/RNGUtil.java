package com.linzhi.gongfu.util;

import java.util.Random;

/**
 * 用于操作生成随机数的工具类
 *
 * @author zhangguanghua
 * @create_at 2021-12-29
 */
public class RNGUtil {
    /**
     * 获取随机字符串 0-9
     * @param length    长度
     * @return 随机数
     */
    public static String getNumber(int length) {
        String str = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            sb.append(str.charAt(random.nextInt(10)));
        }
        return sb.toString();
    }

    /**
     * 获取随机字符串 a-z
     * @param length    长度
     * @return 随机数
     */
    public static String getLowerLetter(int length) {
        String str = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            sb.append(str.charAt(random.nextInt(26)));
        }
        return sb.toString();
    }
}
