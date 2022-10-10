package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.DeliverState;

import javax.persistence.Converter;

/**
 * 转换收货记录状态枚举类型到数据库char类型字段的转换器
 *
 * @author zgh
 * @create_at 2022-05-31
 */
@Converter(autoApply = true)
public class DeliverStateConverter extends CharacterEnumerationConverter<DeliverState> {
    public DeliverStateConverter() throws NoSuchMethodException {
        super(DeliverState.class, "getState");
    }
}
