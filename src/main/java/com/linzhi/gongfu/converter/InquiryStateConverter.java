package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.InquiryState;

import javax.persistence.Converter;

/**
 * 转换询价单中的状态枚举类型到数据库char类型字段的转换器
 * @author xutao
 * @create_at 2021-12-22
 */
@Converter(autoApply = true)
public class InquiryStateConverter extends CharacterEnumerationConverter<InquiryState> {
    public InquiryStateConverter() throws NoSuchMethodException {
        super(InquiryState.class, "getState");
    }
}
