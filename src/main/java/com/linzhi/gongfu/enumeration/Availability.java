package com.linzhi.gongfu.enumeration;

/**
 * 用于表示数据库中使用char型字段记录可用状态的枚举
 * @author xutao
 * @create_at 2021-12-22
 */
public enum Availability {
    DISABLED('0'), ENABLED('1');

    private final char state;

    Availability(char state) {
        this.state = state;
    }

    public char getState() {
        return state;
    }
}
