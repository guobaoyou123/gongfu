package com.linzhi.gongfu.enumeration;

public enum Trade {
    NOT_TRANSACTION("0"), TRANSACTION("1"), APPLY_TRANSACTION("2");

    private final String state;

    Trade(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
