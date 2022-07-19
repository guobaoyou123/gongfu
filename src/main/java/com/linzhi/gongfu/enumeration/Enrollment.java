package com.linzhi.gongfu.enumeration;

/**
 * 公司入格标记
 *
 * @author xutao
 * @create_at 2021-12-22
 */
public enum Enrollment {
    NOT_ENROLLED('0'), MANAGEMENT('3'), ENROLLED('1');

    private final char flag;

    Enrollment(char flag) {
        this.flag = flag;
    }

    public char getFlag() {
        return flag;
    }
}
