package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.DeliverType;

import javax.persistence.Converter;

/**
 * 转换收货记录类型枚举类型到数据库char类型字段的转换器
 * @author zgh
 * @create_at 2022-05-31
 */
@Converter(autoApply = true)
public class DeliverTypeConverter  extends CharacterEnumerationConverter<DeliverType>{
    public DeliverTypeConverter() throws NoSuchMethodException {
        super(DeliverType.class, "getType");
    }
}
