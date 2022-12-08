package com.linzhi.gongfu.mapper.trade;

import com.linzhi.gongfu.dto.TTaxRates;
import com.linzhi.gongfu.entity.TaxRates;
import com.linzhi.gongfu.vo.trade.VTaxRateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaxRatesMapper {

    @Mapping(target = "state", expression = "java(String.valueOf(taxRates.getState().getState()))")
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.getDateTime(taxRates.getCreatedAt()))")
    @Mapping(target = "deflag", expression = "java(String.valueOf(taxRates.getDeFlag().getState()))")
    @Mapping(target = "type", expression = "java(String.valueOf(taxRates.getType().getType()))")
    TTaxRates toTTaxRates(TaxRates taxRates);

    VTaxRateResponse.VTaxRates toVTaxRates(TTaxRates tTaxRates);


}
