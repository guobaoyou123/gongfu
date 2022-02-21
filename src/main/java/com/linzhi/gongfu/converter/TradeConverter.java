package com.linzhi.gongfu.converter;


import com.linzhi.gongfu.enumeration.Trade;

import javax.persistence.Converter;

/**
 * 转换公司交易信息中的交易状态枚举类型到数据库char类型字段的转换
 * @author zgh
 * @create_at 2022-02-18
 */
@Converter(autoApply = true)
public class TradeConverter extends StringEnumerationConverter<Trade> {
    public TradeConverter() throws NoSuchMethodException {
        super(Trade.class, "getState");
    }
}
