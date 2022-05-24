package com.linzhi.gongfu.enumeration;

public enum ContractState {
    UN_FINISHED('0'), FINISHED('1'), CANCELLATION('2');

    private final char state;

    ContractState(char state) {
        this.state = state;
    }

    public char getState() {
        return state;
    }
}
