package com.linzhi.gongfu.enumeration;
/**
 * 平台所记录的货运记录状态
 *
 * @author zgh
 * @created_at 2022-05-31
 */
public enum DeliverState {
   PENDING('0'), PROCESSED('1');

    private final char state;

    DeliverState(char state) {
        this.state = state;
    }

    public char getState() {
        return state;
    }
}
