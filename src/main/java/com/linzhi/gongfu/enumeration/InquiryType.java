package com.linzhi.gongfu.enumeration;

public enum InquiryType {
    INQUIRY_LIST('0'),  QUOTATION('1');

    private final char type;

    InquiryType(char type) {
        this.type = type;
    }

    public char getType() {
        return type;
    }
}
