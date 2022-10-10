package com.linzhi.gongfu.enumeration;

/**
 * 平台所记录的货运记录类型
 * 1-收货 2-发货
 *
 * @author zgh
 * @created_at 2022-05-31
 */
public enum DeliverType {
    DELIVER('2'), RECEIVE('1');

    private final char type;

    DeliverType(char type) {
        this.type = type;
    }

    public char getType() {
        return type;
    }
}
