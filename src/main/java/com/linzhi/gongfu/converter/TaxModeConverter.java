package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.TaxMode;

import javax.persistence.Converter;

/**
 * 转换枚举型税模式值到数据库的char类型字段
 *
 * @author xutao
 * @create_at 2021-12-23
 */
@Converter(autoApply = true)
public class TaxModeConverter extends CharacterEnumerationConverter<TaxMode> {
    public TaxModeConverter() throws NoSuchMethodException {
        super(TaxMode.class, "getTaxMode");
    }
}
