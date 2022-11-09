package com.linzhi.gongfu.enumeration;

/**
 * 报价状态（待报价0-待报价 1-已报价 2-已废弃 3-已经生成销售合同）
 */
public enum OfferType {

    WAIT_OFFER('0'), FINISH_OFFER('1'), ABANDONED_OFFER('2'),GENERATE_CONTRACT('3')
    ;

    private final char type;

    OfferType(char type) {
        this.type = type;
    }

    public char getType() {
        return type;
    }
}
