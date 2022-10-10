package com.linzhi.gongfu.converter;


import com.linzhi.gongfu.enumeration.TradeApply;

import javax.persistence.Converter;

/**
 * 转换枚举型申请采购状态到数据库的char类型字段
 *
 * @author zhangguanghua
 * @create_at 2022-07-19
 */
@Converter(autoApply = true)
public class TradeApplyConverter extends CharacterEnumerationConverter<TradeApply> {
    public TradeApplyConverter() throws NoSuchMethodException {
        super(TradeApply.class, "getState");
    }
}
