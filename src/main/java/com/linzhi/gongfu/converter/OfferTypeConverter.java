package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.OfferType;

import javax.persistence.Converter;

/**
 * 转换报价状态枚举类型到数据库char型字段值
 *
 * @author zgh
 * @create_at 2022-11-09
 */
@Converter(autoApply = true)
public class OfferTypeConverter extends CharacterEnumerationConverter<OfferType> {
    public OfferTypeConverter() throws NoSuchMethodException {
        super(OfferType.class, "getState");
    }
}
