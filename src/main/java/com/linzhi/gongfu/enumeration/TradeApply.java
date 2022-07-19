package com.linzhi.gongfu.enumeration;

public enum TradeApply {
    APPLYING("0"), AGREE("1"), REFUSE("2");

    private final String state;

    TradeApply(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
