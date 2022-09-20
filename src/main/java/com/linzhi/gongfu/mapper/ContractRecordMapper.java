package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TContractRecord;
import com.linzhi.gongfu.dto.TContractRecordPreview;
import com.linzhi.gongfu.entity.*;
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

    /**
     * 将从数据库中查找到的销售合同明细转换为可供使用的销售合同明细
     * @param contractRecord 销售合同明细
     * @return 可供使用的销售合同明细
     */
    @Mapping(target = "itemNo", source = "salesContractRecordId.code")
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(contractRecord.getCreatedAt()))")
    @Mapping(target = "id", source = "productId")
    @Mapping(target = "code", source = "productCode")
    @Mapping(target = "describe", source = "productDescription")
    @Mapping(target = "brandCode", source = "brandCode")
    @Mapping(target = "brandName", source = "brand")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "priceVat", source = "priceVat")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "totalPriceVat", source = "totalPriceVat")
    @Mapping(target = "vatRate", source = "vatRate")
    @Mapping(target = "chargeUnit", source = "chargeUnit")
    @Mapping(target = "type", expression = "java(String.valueOf(contractRecord.getType().getType()))")
    @Mapping(target = "facePrice", source = "facePrice")
    TContractRecord toTContractRecord(SalesContractRecord contractRecord);

    /**
     * 将从数据库中查找到的临时销售合同明细转换为可供使用的临时销售合同明细
     * @param contractRecordTemp 临时销售合同明细
     * @return 可供使用的临时销售合同明细
     */
    @Mapping(target = "itemNo", source = "salesContractRecordTempId.code")
    @Mapping(target = "createdAt", expression = "java(contractRecordTemp.getCreatedAt()!=null?com.linzhi.gongfu.util.DateConverter.dateFormat(contractRecordTemp.getCreatedAt()):null)")
    @Mapping(target = "id", source = "productId")
    @Mapping(target = "code", source = "productCode")
    @Mapping(target = "describe", source = "productDescription")
    @Mapping(target = "brandCode", source = "brandCode")
    @Mapping(target = "brandName", source = "brand")
    @Mapping(target = "amount", source = "amount")
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
    @Mapping(target = "chargeUnit", source = "chargeUnit")
    @Mapping(target = "previousChargeUnit", source = "previousChargeUnit")
    @Mapping(target = "type", expression = "java(String.valueOf(contractRecordTemp.getType().getType()))")
    @Mapping(target = "facePrice", source = "facePrice")
    @Mapping(target = "previousAmount", source = "previousAmount")
    TContractRecord toTContractRecord(SalesContractRecordTemp contractRecordTemp);

    /**
     * 将产品明细实体转换为产品临时明细
     * @param contractRecord 产品明细实体
     * @return 产品临时明细实体
     */
    @Mapping(target = "salesContractRecordTempId.contractId", source = "salesContractRecordId.contractId")
    @Mapping(target = "salesContractRecordTempId.code", source = "salesContractRecordId.code")
    @Mapping(target = "previousVatRate", source = "vatRate")
    @Mapping(target = "previousChargeUnit", source = "chargeUnit")
    @Mapping(target = "previousPrice", source = "price")
    @Mapping(target = "previousPriceVat", source = "priceVat")
    @Mapping(target = "previousAmount", source = "amount")
    @Mapping(target = "totalPreviousPrice", source = "totalPrice")
    @Mapping(target = "totalPreviousPriceVat", source = "totalPriceVat")
    @Mapping(target = "vatRate", source = "vatRate")
    @Mapping(target = "ratio", source = "ratio")
    @Mapping(target = "sysChargeUnit", source = "sysChargeUnit")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "priceVat", source = "priceVat")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "sysAmount", source = "sysAmount")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "totalPriceVat", source = "totalPriceVat")
    @Mapping(target = "previousRemark", source = "remark")
    @Mapping(target = "remark", source = "remark")
    @Mapping(target = "salesContractRecordTempId.revision", expression = "java(salesContractRecordId.getRevision()+1)")
    SalesContractRecordTemp toContractRecordTemp(SalesContractRecord contractRecord);

    /**
     * 將销售合同临时明细转换为合同明细
     * @param contractRecord 临时明细
     * @return 合同明细
     */
    @Mapping(target = "salesContractRecordId.contractId", source = "salesContractRecordTempId.contractId")
    @Mapping(target = "salesContractRecordId.code", source = "salesContractRecordTempId.code")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "priceVat", source = "priceVat")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "totalPriceVat", source = "totalPriceVat")
    @Mapping(target = "vatRate", source = "vatRate")
    @Mapping(target = "ratio", source = "ratio")
    @Mapping(target = "sysChargeUnit", source = "sysChargeUnit")
    @Mapping(target = "chargeUnit", source = "chargeUnit")
    @Mapping(target = "salesContractRecordId.revision", source = "salesContractRecordTempId.revision")
    @Mapping(target = "specification", source = "specification")
    @Mapping(target = "remark", source = "remark")
    SalesContractRecord toSalesContractRecord(SalesContractRecordTemp contractRecord);
}
