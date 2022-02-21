package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.VatRateType;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class VatRateTypeConverter  extends CharacterEnumerationConverter<VatRateType> {
    public VatRateTypeConverter() throws NoSuchMethodException {
        super(VatRateType.class, "getType");
    }
}
