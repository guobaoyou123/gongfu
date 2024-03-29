package com.linzhi.gongfu.enumeration;

/**
 * 用于表示数据库中使用char类型字段表示税模式的枚举
 *
 * @author xutao
 * @create_at 2021-12-23
 */
public enum TaxMode {
    INCLUDED('1'), UNTAXED('0');

    private final char taxMode;

    TaxMode(char taxMode) {
        this.taxMode = taxMode;
    }

    public char getTaxMode() {
        return taxMode;
    }
}
