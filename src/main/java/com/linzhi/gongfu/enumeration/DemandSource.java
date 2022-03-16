package com.linzhi.gongfu.enumeration;
/**
 * 用于平台记录需求来源
 * @author zgh
 * @create_at 2022-02-14
 */
public enum DemandSource {
    FUZZY_QUERY('1'), INDIVIDUAL_PROCUREMENT('2'), IMPORT_DEMAND('3'), RUN_DEMAND('4'),NEW_DEMAND('5');

    private final char source;

    DemandSource(char source) {
        this.source = source;
    }

    public char getSource() {
        return source;
    }
}
