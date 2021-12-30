package com.linzhi.gongfu.enumeration;

/**
 * 公司入格标记
 * @author xutao
 * @create_at 2021-12-22
 */
public enum Enrollment {
    NOT_ENROLLED('1'), MANAGEMENT('2'), ENROLLED('3');

    private final char flag;

    Enrollment(char flag) {
        this.flag = flag;
    }

    public char getFlag() {
        return flag;
    }
}
