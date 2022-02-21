package com.linzhi.gongfu.enumeration;

public enum Trade {
    NOT_TRANSACTION('0'), TRANSACTION('1'), APPLY_TRANSACTION('2');

    private final char state;

    Trade(char state) {
        this.state = state;
    }

    public char getState() {
        return state;
    }
}
