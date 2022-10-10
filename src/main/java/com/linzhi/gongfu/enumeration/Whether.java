package com.linzhi.gongfu.enumeration;

/**
 * 用于表示数据库中使用char型字段记录是否状态的枚举
 *
 * @author xutao
 * @create_at 2021-12-15
 */
public enum Whether {
    YES('1'), NO('0');

    private final char state;

    private Whether(char state) {
        this.state = state;
    }

    public char getState() {
        return this.state;
    }
}
