package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.DateType;

import javax.persistence.Converter;

/**
 * 转换可用性枚举类型时间格式
 * @author zgh
 * @create_at 2022-06-06
 */
@Converter(autoApply = true)
public class DateTypeConverter extends CharacterEnumerationConverter<DateType> {
    public DateTypeConverter() throws NoSuchMethodException {
        super(DateType.class, "getType");
    }
}
