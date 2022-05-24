package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.ContractState;
import com.linzhi.gongfu.enumeration.InquiryState;

import javax.persistence.Converter;

/**
 * 转换采购合同中的状态枚举类型到数据库char类型字段的转换器
 * @author zgh
 * @create_at 2022-05-24
 */
@Converter(autoApply = true)
public class ContractStateConverter  extends CharacterEnumerationConverter<ContractState>{
    public ContractStateConverter() throws NoSuchMethodException {
        super(ContractState.class, "getState");
    }
}
