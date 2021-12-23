package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.Whether;

import javax.persistence.Converter;

/**
 * 转换是否枚举类型到数据库char型字段值
 * @author xutao
 * @create_at 2021-12-15
 */
@Converter(autoApply = true)
public class WhetherConverter extends CharacterEnumerationConverter<Whether> {
    public WhetherConverter() throws NoSuchMethodException {
        super(Whether.class, "getState");
    }
}
