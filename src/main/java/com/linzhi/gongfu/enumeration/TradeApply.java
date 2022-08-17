package com.linzhi.gongfu.enumeration;

public enum TradeApply {
    APPLYING('0'), AGREE('1'), REFUSE('2'), ALWAYS_REFUSE('3');

    private final char state;

    TradeApply(char state) {
        this.state = state;
    }

    public char getState() {
        return state;
    }
}
