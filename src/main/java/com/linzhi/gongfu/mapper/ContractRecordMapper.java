package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TContractRecord;
import com.linzhi.gongfu.entity.ContractRecord;
import com.linzhi.gongfu.entity.ContractRecordTemp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContractRecordMapper {

    @Mapping(target = "itemNo",source = "contractRecordId.code")
    @Mapping(target = "createdAt",expression ="java(com.linzhi.gongfu.util.DateConverter.dateFormat(contractRecord.getCreatedAt()))" )
    @Mapping(target = "id",source = "productId")
    @Mapping(target = "code",source = "productCode")
    @Mapping(target = "describe",source = "productDescription")
    @Mapping(target = "brandCode",source = "brandCode")
    @Mapping(target = "brandName",source = "brand")
    @Mapping(target = "amount",source = "myAmount")
    @Mapping(target = "price",source = "price")
    @Mapping(target = "priceVat",source = "priceVat")
    @Mapping(target = "totalPrice",source = "totalPrice")
    @Mapping(target = "totalPriceVat",source = "totalPriceVat")
    @Mapping(target = "vatRate",source = "vatRate")
    @Mapping(target = "stockTime",source = "stockTime")
    @Mapping(target = "chargeUnit",source = "myChargeUnit")
    @Mapping(target = "type",expression = "java(String.valueOf(contractRecord.getType().getType()))")
    @Mapping(target = "facePrice",source = "facePrice")
    TContractRecord toTContractRecord(ContractRecord contractRecord);
    @Mapping(target = "itemNo",source = "contractRecordId.code")
    @Mapping(target = "createdAt",expression ="java(com.linzhi.gongfu.util.DateConverter.dateFormat(contractRecordTemp.getCreatedAt()))" )
    @Mapping(target = "id",source = "productId")
    @Mapping(target = "code",source = "productCode")
    @Mapping(target = "describe",source = "productDescription")
    @Mapping(target = "brandCode",source = "brandCode")
    @Mapping(target = "brandName",source = "brand")
    @Mapping(target = "amount",source = "myAmount")
    @Mapping(target = "price",source = "price")
    @Mapping(target = "priceVat",source = "priceVat")
    @Mapping(target = "totalPrice",source = "totalPrice")
    @Mapping(target = "totalPriceVat",source = "totalPriceVat")
    @Mapping(target = "previousPrice",source = "previousPrice")
    @Mapping(target = "previousTotalPrice",source = "totalPreviousPrice")
    @Mapping(target = "previousPriceVat",source = "previousPriceVat")
    @Mapping(target = "previousTotalPriceVat",source = "totalPreviousPriceVat")
    @Mapping(target = "vatRate",source = "vatRate")
    @Mapping(target = "previousVatRate",source = "previousVatRate")
    @Mapping(target = "stockTime",source = "stockTime")
    @Mapping(target = "chargeUnit",source = "myChargeUnit")
    @Mapping(target = "previousChargeUnit",source = "previousMyChargeUnit")
    @Mapping(target = "type",expression = "java(String.valueOf(contractRecordTemp.getType().getType()))")
    @Mapping(target = "facePrice",source = "facePrice")
    @Mapping(target = "previousAmount",source = "previousMyAmount")
    TContractRecord toTContractRecord(ContractRecordTemp contractRecordTemp);
}
