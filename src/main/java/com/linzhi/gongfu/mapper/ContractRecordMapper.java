package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TContractRecord;
import com.linzhi.gongfu.dto.TContractRecordPreview;
import com.linzhi.gongfu.entity.PurchaseContractRecord;
import com.linzhi.gongfu.entity.ContractRecordPreview;
import com.linzhi.gongfu.entity.PurchaseContractRecordTemp;
import com.linzhi.gongfu.vo.VPContractPreviewResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContractRecordMapper {

    @Mapping(target = "itemNo", source = "purchaseContractRecordId.code")
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(contractRecord.getCreatedAt()))")
    @Mapping(target = "id", source = "productId")
    @Mapping(target = "code", source = "productCode")
    @Mapping(target = "describe", source = "productDescription")
    @Mapping(target = "brandCode", source = "brandCode")
    @Mapping(target = "brandName", source = "brand")
    @Mapping(target = "amount", source = "myAmount")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "priceVat", source = "priceVat")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "totalPriceVat", source = "totalPriceVat")
    @Mapping(target = "vatRate", source = "vatRate")
    @Mapping(target = "stockTime", source = "stockTime")
    @Mapping(target = "chargeUnit", source = "myChargeUnit")
    @Mapping(target = "type", expression = "java(String.valueOf(contractRecord.getType().getType()))")
    @Mapping(target = "facePrice", source = "facePrice")
    TContractRecord toTContractRecord(PurchaseContractRecord contractRecord);

    @Mapping(target = "itemNo", source = "purchaseContractRecordTempId.code")
    @Mapping(target = "createdAt", expression = "java(contractRecordTemp.getCreatedAt()!=null?com.linzhi.gongfu.util.DateConverter.dateFormat(contractRecordTemp.getCreatedAt()):null)")
    @Mapping(target = "id", source = "productId")
    @Mapping(target = "code", source = "productCode")
    @Mapping(target = "describe", source = "productDescription")
    @Mapping(target = "brandCode", source = "brandCode")
    @Mapping(target = "brandName", source = "brand")
    @Mapping(target = "amount", source = "myAmount")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "priceVat", source = "priceVat")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "totalPriceVat", source = "totalPriceVat")
    @Mapping(target = "previousPrice", source = "previousPrice")
    @Mapping(target = "previousTotalPrice", source = "totalPreviousPrice")
    @Mapping(target = "previousPriceVat", source = "previousPriceVat")
    @Mapping(target = "previousTotalPriceVat", source = "totalPreviousPriceVat")
    @Mapping(target = "vatRate", source = "vatRate")
    @Mapping(target = "previousVatRate", source = "previousVatRate")
    @Mapping(target = "stockTime", source = "stockTime")
    @Mapping(target = "chargeUnit", source = "myChargeUnit")
    @Mapping(target = "previousChargeUnit", source = "previousMyChargeUnit")
    @Mapping(target = "type", expression = "java(String.valueOf(contractRecordTemp.getType().getType()))")
    @Mapping(target = "facePrice", source = "facePrice")
    @Mapping(target = "previousAmount", source = "previousMyAmount")
    TContractRecord toTContractRecord(PurchaseContractRecordTemp contractRecordTemp);

    @Mapping(target = "purchaseContractRecordTempId.contractId", source = "purchaseContractRecordId.contractId")
    @Mapping(target = "purchaseContractRecordTempId.code", source = "purchaseContractRecordId.code")
    @Mapping(target = "previousVatRate", source = "vatRate")
    @Mapping(target = "previousRatio", source = "ratio")
    @Mapping(target = "previousMyChargeUnit", source = "myChargeUnit")
    @Mapping(target = "previousPrice", source = "price")
    @Mapping(target = "previousPriceVat", source = "priceVat")
    @Mapping(target = "previousAmount", source = "amount")
    @Mapping(target = "previousMyAmount", source = "myAmount")
    @Mapping(target = "totalPreviousPrice", source = "totalPrice")
    @Mapping(target = "totalPreviousPriceVat", source = "totalPriceVat")
    @Mapping(target = "vatRate", source = "vatRate")
    @Mapping(target = "ratio", source = "ratio")
    @Mapping(target = "myChargeUnit", source = "myChargeUnit")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "priceVat", source = "priceVat")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "myAmount", source = "myAmount")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "totalPriceVat", source = "totalPriceVat")
    @Mapping(target = "purchaseContractRecordTempId.revision", expression = "java(purchaseContractRecordId.getRevision()+1)")
    PurchaseContractRecordTemp toContractRecordTemp(PurchaseContractRecord contractRecord);

    @Mapping(target = "purchaseContractRecordId.contractId", source = "purchaseContractRecordTempId.contractId")
    @Mapping(target = "purchaseContractRecordId.code", source = "purchaseContractRecordTempId.code")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "priceVat", source = "priceVat")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "myAmount", source = "myAmount")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "totalPriceVat", source = "totalPriceVat")
    @Mapping(target = "vatRate", source = "vatRate")
    @Mapping(target = "ratio", source = "ratio")
    @Mapping(target = "myChargeUnit", source = "myChargeUnit")
    @Mapping(target = "purchaseContractRecordId.revision", source = "purchaseContractRecordTempId.revision")
    PurchaseContractRecord toContractRecord(PurchaseContractRecordTemp contractRecord);

    @Mapping(target = "received", expression = "java(contractRecordPreview.getDelivered()!=null?contractRecordPreview.getDelivered().subtract(contractRecordPreview.getReceived()):null)")
    TContractRecordPreview toTContractRecordPreview(ContractRecordPreview contractRecordPreview);


    @Mapping(target = "receivedAmount", source = "received")
    VPContractPreviewResponse.VProduct toVProduct(TContractRecordPreview tContractRecordPreview);
}
