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
    @Mapping(target = "itemNo",source = "contractRecordTempId.code")
    @Mapping(target = "createdAt",expression ="java(contractRecordTemp.getCreatedAt()!=null?com.linzhi.gongfu.util.DateConverter.dateFormat(contractRecordTemp.getCreatedAt()):null)" )
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
    @Mapping(target = "contractRecordTempId.contractId",source = "contractRecordId.contractId")
    @Mapping(target = "contractRecordTempId.code",source = "contractRecordId.code")
    @Mapping(target = "previousVatRate",source = "vatRate")
    @Mapping(target = "previousRatio",source = "ratio")
    @Mapping(target = "previousMyChargeUnit",source = "myChargeUnit")
    @Mapping(target = "previousPrice",source = "price")
    @Mapping(target = "previousPriceVat",source = "priceVat")
    @Mapping(target = "previousAmount",source = "amount")
    @Mapping(target = "previousMyAmount",source = "myAmount")
    @Mapping(target = "totalPreviousPrice",source = "totalPrice")
    @Mapping(target = "totalPreviousPriceVat",source = "totalPriceVat")
    @Mapping(target = "vatRate",source = "vatRate")
    @Mapping(target = "ratio",source = "ratio")
    @Mapping(target = "myChargeUnit",source = "myChargeUnit")
    @Mapping(target = "price",source = "price")
    @Mapping(target = "priceVat",source = "priceVat")
    @Mapping(target = "amount",source = "amount")
    @Mapping(target = "myAmount",source = "myAmount")
    @Mapping(target = "totalPrice",source = "totalPrice")
    @Mapping(target = "totalPriceVat",source = "totalPriceVat")
    @Mapping(target = "contractRecordTempId.revision",expression = "java(contractRecordId.getRevision()+1)")
    ContractRecordTemp toContractRecordTemp(ContractRecord contractRecord);

}
