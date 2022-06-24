package com.linzhi.gongfu.mapper;


import com.linzhi.gongfu.dto.TContract;
import com.linzhi.gongfu.dto.TContractReceived;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.vo.VPurchaseContractDetailResponse;
import com.linzhi.gongfu.vo.VPurchaseContractPageResponse;
import com.linzhi.gongfu.vo.VReceivedResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换合同相关信息
 *
 * @author xutao
 * @create_at 2022-05-24
 */
@Mapper(componentModel = "spring")
public interface ContractMapper {


    /**
     * 转换询价单列表
     * @param contract 采购合同基本信息
     * @return 采购合同基本信息
     */
    @Mapping(target = "createdAt",expression ="java(com.linzhi.gongfu.util.DateConverter.dateFormat(contract.getCreatedAt()))" )
    @Mapping(target = "type",expression = "java(String.valueOf(contract.getType().getType()))")
    @Mapping(target = "state",expression = "java(String.valueOf(contract.getState().getState()))")
    @Mapping(target = "salerCompName",source = "salerCompNameShort")
    TContract toContractList(ContractList contract);

    /**
     * 转换采购合同列表
     * @param tContract 采购合同基本信息
     * @return 采购合同基本信息
     */
    @Mapping(target = "supplierName",source = "salerCompName")
    @Mapping(target = "ownerCode",source = "createdBy")
    @Mapping(target = "ownerName",source = "createdByName")
    @Mapping(target = "salesContractId",source = "salesContractId")
    @Mapping(target = "salesContractCode",source = "salesContractCode")
    @Mapping(target = "salesContractNo",source = "salesOrderCode")
    @Mapping(target = "paired",constant = "false")
    @Mapping(target = "contractNo",source = "orderCode")
    @Mapping(target = "taxedTotal",source = "taxedTotal")
    @Mapping(target = "confirmTaxedTotal",source = "confirmTaxedTotal")
    VPurchaseContractPageResponse.VContract toContractPage(TContract tContract);

    @Mapping(target = "id",source = "contractRevisionId.id")
    @Mapping(target = "revision",source = "contractRevisionId.revision")
    @Mapping(target = "code",source = "code")
    @Mapping(target = "orderCode",source = "orderCode")
    @Mapping(target = "supplierContractNo",source = "salerOrderCode")
    @Mapping(target = "salesContractId",source = "salesContractId")
    @Mapping(target = "salesContractCode",source = "salesContractCode")
    @Mapping(target = "salesOrderCode",source = "salesOrderCode")
    @Mapping(target = "createdByComp",source = "createdByComp")
    @Mapping(target = "createdBy",source = "createdBy")
    @Mapping(target = "createdByName",source = "createdByName")
    @Mapping(target = "buyerComp",source = "buyerComp")
    @Mapping(target = "buyerCompName",source = "buyerCompName")
    @Mapping(target = "salerComp",source = "salerComp")
    @Mapping(target = "salerCompName",source = "salerCompName")
    @Mapping(target = "createdAt",expression ="java(com.linzhi.gongfu.util.DateConverter.dateFormat(contractRevisionDetail.getCreatedAt()))" )
    @Mapping(target = "offerMode",expression = "java(String.valueOf(contractRevisionDetail.getOfferMode().getTaxMode()))")
    @Mapping(target = "tax",source = "vat")
    @Mapping(target = "untaxedTotal",source = "totalPrice")
    @Mapping(target = "taxedTotal",source = "totalPriceVat")
    @Mapping(target = "previousUntaxedTotal",source = "previousUntaxedTotal")
    @Mapping(target = "previousTaxedTotal",source = "previousTaxedTotal")
    @Mapping(target = "supplierContactName",source = "salerContactName")
    @Mapping(target = "supplierContactPhone",source = "salerContactPhone")
    @Mapping(target = "goodsVat",source = "vatProductRate")
    @Mapping(target = "serviceVat",source = "vatServiceRate")
    @Mapping(target = "areaCode",source = "areaCode")
    @Mapping(target = "areaName",source = "areaName")
    @Mapping(target = "address",source = "address")
    @Mapping(target = "consigneeName",source = "consigneeName")
    @Mapping(target = "consigneePhone",source = "consigneePhone")
    @Mapping(target = "confirmTaxedTotal",source = "confirmTotalPriceVat")
    @Mapping(target = "discount",source = "discount")
    @Mapping(target = "discountedTotalPrice",source = "discountedTotalPrice")
    TContract toTContractDetail(ContractRevisionDetail contractRevisionDetail);

    @Mapping(target = "contractNo",source = "orderCode")
    @Mapping(target = "supplierNo",source = "supplierContractNo")
    @Mapping(target = "salesContractId",source = "salesContractId")
    @Mapping(target = "salesContractCode",source = "salesContractCode")
    @Mapping(target = "salesContractNo",source = "salesOrderCode")
    @Mapping(target = "ownerCode",source = "createdBy")
    @Mapping(target = "ownerName",source = "createdByName")
    @Mapping(target = "supplierCode",source = "salerComp")
    @Mapping(target = "supplierName",source = "salerCompName")
    @Mapping(target = "products",source = "records")
    @Mapping(target = "consigneeCode",source = "contactCode")
    VPurchaseContractDetailResponse.VContract toContractDetail(TContract tContract);

    ContractRevision toContractRevision(ContractRevisionDetail contractRevisionDetail);

    @Mapping(target = "received",expression = "java(contractReceived.getDelivered()!=null?contractReceived.getReceived()!=null?contractReceived.getDelivered().subtract(contractReceived.getReceived()):contractReceived.getDelivered():null)")
    TContractReceived toTContractReceived(ContractReceived contractReceived);
    @Mapping(target = "receivedAmount",source = "received")
    VReceivedResponse.VProduct toVProduct(TContractReceived tContractReceived);

    @Mapping(target ="id" ,constant = "")
    @Mapping(target = "code",ignore = true)
    @Mapping(target = "salesContractId",ignore = true)
    ContractDetail toContractDetail(ContractDetail contractDetail);

    @Mapping(target ="contractRevisionId.id" ,ignore = true)
    @Mapping(target = "contractRevisionId.revision",constant = "1")
    @Mapping(target = "orderCode",ignore = true)
    @Mapping(target  ="salerOrderCode",ignore = true)
    @Mapping(target = "revokedAt",ignore = true)
    @Mapping(target = "revokedBy",ignore = true)
    @Mapping(target = "modifiedAt",ignore = true)
    @Mapping(target = "modifiedBy",ignore = true)
    @Mapping(target = "confirmedAt",ignore = true)
    @Mapping(target = "confirmedBy",ignore = true)
    @Mapping(target = "contractRecords",ignore = true)
    ContractRevision toContractRevision(ContractRevision contractRevision);
}
