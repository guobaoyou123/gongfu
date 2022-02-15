package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.DemandSource;

import javax.persistence.Converter;

/**
 * 转换可用性枚举类型到数据库char型字段值
 * @author zgh
 * @create_at 2022-02-14
 */
@Converter(autoApply = true)
public class DemandSourceConverter extends CharacterEnumerationConverter<DemandSource>{
    public DemandSourceConverter() throws NoSuchMethodException {
        super(DemandSource.class, "getSource");
    }
}
