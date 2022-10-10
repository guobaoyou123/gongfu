package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.Availability;

import javax.persistence.Converter;

/**
 * 转换可用性枚举类型到数据库char型字段值
 *
 * @author xutao
 * @create_at 2021-12-22
 */
@Converter(autoApply = true)
public class AvailabilityConverter extends CharacterEnumerationConverter<Availability> {
    public AvailabilityConverter() throws NoSuchMethodException {
        super(Availability.class, "getState");
    }
}
