package com.linzhi.gongfu.mapper;


import com.linzhi.gongfu.dto.TContract;
import com.linzhi.gongfu.dto.TContractReceived;
import com.linzhi.gongfu.dto.TRevision;
import com.linzhi.gongfu.dto.TSalesContracts;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.vo.VPContractDetailResponse;
import com.linzhi.gongfu.vo.VPContractPageResponse;
import com.linzhi.gongfu.vo.VReceivedResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 用于转换合同相关信息
 *
 * @author xutao
 * @create_at 2022-05-24
 */
@Mapper(componentModel = "spring")
public interface ContractMapper {


    /**
     * 将获取到的采购合同列表转换成可供使用的采购合同列表
     *
     * @param contract 采购合同基本信息
     * @return 采购合同基本信息
     */
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(contract.getCreatedAt()))")
    @Mapping(target = "state", expression = "java(String.valueOf(contract.getState().getState()))")
    @Mapping(target = "salerCompName", source = "salerCompNameShort")
    TContract toContractList(PurchaseContractList contract);

    /**
     * 转换采购合同列表
     *
     * @param tContract 采购合同基本信息
     * @return 采购合同基本信息
     */
    @Mapping(target = "supplierName", source = "salerCompName")
    @Mapping(target = "customerName", source = "buyerCompName")
    @Mapping(target = "ownerCode", source = "createdBy")
    @Mapping(target = "ownerName", source = "createdByName")
    @Mapping(target = "salesContractId", source = "salesContractId")
    @Mapping(target = "salesContractCode", source = "salesContractCode")
    @Mapping(target = "salesContractNo", source = "salesOrderCode")
    @Mapping(target = "paired", source = "paired")
    @Mapping(target = "contractNo", source = "orderCode")
    @Mapping(target = "taxedTotal", source = "taxedTotal")
    @Mapping(target = "confirmTaxedTotal", source = "confirmTaxedTotal")
    VPContractPageResponse.VContract toContractPage(TContract tContract);

    @Mapping(target = "id", source = "purchaseContractRevisionId.id")
    @Mapping(target = "revision", source = "purchaseContractRevisionId.revision")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "orderCode", source = "orderCode")
    @Mapping(target = "supplierContractNo", source = "salerOrderCode")
    @Mapping(target = "salesContractId", source = "salesContractId")
    @Mapping(target = "salesContractCode", source = "salesContractCode")
    @Mapping(target = "salesOrderCode", source = "salesOrderCode")
    @Mapping(target = "createdByComp", source = "createdByComp")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdByName", source = "createdByName")
    @Mapping(target = "buyerComp", source = "buyerComp")
    @Mapping(target = "buyerCompName", source = "buyerCompName")
    @Mapping(target = "salerComp", source = "salerComp")
    @Mapping(target = "salerCompName", source = "salerCompName")
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(contractRevisionDetail.getCreatedAt()))")
    @Mapping(target = "offerMode", expression = "java(String.valueOf(contractRevisionDetail.getOfferMode().getTaxMode()))")
    @Mapping(target = "tax", source = "vat")
    @Mapping(target = "untaxedTotal", source = "totalPrice")
    @Mapping(target = "taxedTotal", source = "totalPriceVat")
    @Mapping(target = "previousUntaxedTotal", source = "previousUntaxedTotal")
    @Mapping(target = "previousTaxedTotal", source = "previousTaxedTotal")
    @Mapping(target = "supplierContactName", source = "salerContactName")
    @Mapping(target = "supplierContactPhone", source = "salerContactPhone")
    @Mapping(target = "goodsVat", source = "vatProductRate")
    @Mapping(target = "serviceVat", source = "vatServiceRate")
    @Mapping(target = "areaCode", source = "areaCode")
    @Mapping(target = "areaName", source = "areaName")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "consigneeName", source = "consigneeName")
    @Mapping(target = "consigneePhone", source = "consigneePhone")
    @Mapping(target = "confirmTaxedTotal", source = "confirmTotalPriceVat")
    @Mapping(target = "discount", source = "discount")
    @Mapping(target = "discountedTotalPrice", source = "discountedTotalPrice")
    TContract toTContractDetail(PurchaseContractRevisionDetail contractRevisionDetail);

    @Mapping(target = "contractNo", source = "orderCode")
    @Mapping(target = "supplierNo", source = "supplierContractNo")
    @Mapping(target = "salesContractId", source = "salesContractId")
    @Mapping(target = "salesContractCode", source = "salesContractCode")
    @Mapping(target = "salesContractNo", source = "salesOrderCode")
    @Mapping(target = "ownerCode", source = "createdBy")
    @Mapping(target = "ownerName", source = "createdByName")
    @Mapping(target = "supplierCode", source = "salerComp")
    @Mapping(target = "supplierName", source = "salerCompName")
    @Mapping(target = "customerNo", source = "customerContractNo")
    @Mapping(target = "customerCode", source = "buyerComp")
    @Mapping(target = "customerName", source = "buyerCompName")
    @Mapping(target = "products", source = "records")
    @Mapping(target = "consigneeCode", source = "contactCode")
    VPContractDetailResponse.VContract toContractDetail(TContract tContract);

    PurchaseContractRevision toContractRevision(PurchaseContractRevisionDetail contractRevisionDetail);

    @Mapping(target = "received", expression = "java(contractReceived.getDelivered()!=null?contractReceived.getReceived()!=null?contractReceived.getDelivered().subtract(contractReceived.getReceived()):contractReceived.getDelivered():null)")
    TContractReceived toTContractReceived(ContractReceived contractReceived);

    @Mapping(target = "receivedAmount", source = "received")
    VReceivedResponse.VProduct toVProduct(TContractReceived tContractReceived);

    @Mapping(target = "id", constant = "")
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "salesContractId", ignore = true)
    PurchaseContractDetail toContractDetail(PurchaseContractDetail contractDetail);

    @Mapping(target = "purchaseContractRevisionId.id", ignore = true)
    @Mapping(target = "purchaseContractRevisionId.revision", constant = "1")
    @Mapping(target = "orderCode", ignore = true)
    @Mapping(target = "salerOrderCode", ignore = true)
    @Mapping(target = "revokedAt", ignore = true)
    @Mapping(target = "revokedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "confirmedAt", ignore = true)
    @Mapping(target = "confirmedBy", ignore = true)
    @Mapping(target = "contractRecords", ignore = true)
    PurchaseContractRevision toContractRevision(PurchaseContractRevision contractRevision);

    /**
     * 将获取到的销售合同列表转换成可供使用的销售合同列表
     *
     * @param contract 销售合同基本信息
     * @return 销售合同基本信息
     */
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(contract.getCreatedAt()))")
    @Mapping(target = "buyerCompName", source = "buyerCompName")
    @Mapping(target = "state", expression = "java(String.valueOf(contract.getState().getState()))")
    @Mapping(target = "paired",expression = "java(contract.getPaired()==null?false:true)")
    TContract toContractList(SalesContracts contract);

    /**
     * 将获取到的销售合同版本详情转换成可供使用的销售合同版本详情
     *
     * @param contractRevisionDetail 销售合同版本详情
     * @return 可供使用的销售合同基本信息
     */
    @Mapping(target = "id", source = "salesContractRevisionId.id")
    @Mapping(target = "revision", source = "salesContractRevisionId.revision")
    @Mapping(target = "code", source = "salesContractBase.code")
    @Mapping(target = "orderCode", source = "orderCode")
    @Mapping(target = "customerContractNo", source = "buyerOrderCode")
    @Mapping(target = "createdByComp", source = "salesContractBase.createdByComp")
    @Mapping(target = "createdBy", source = "salesContractBase.createdBy")
    @Mapping(target = "createdByName", source = "salesContractBase.operator.name")
    @Mapping(target = "buyerComp", source = "salesContractBase.buyerComp")
    @Mapping(target = "buyerCompName", source = "salesContractBase.buyerCompName")
    @Mapping(target = "salerComp", source = "salesContractBase.salerComp")
    @Mapping(target = "salerCompName", source = "salesContractBase.salerCompName")
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(contractRevisionDetail.getSalesContractBase().getCreatedAt()))")
    @Mapping(target = "offerMode", expression = "java(String.valueOf(contractRevisionDetail.getOfferMode().getTaxMode()))")
    @Mapping(target = "state", expression = "java( String.valueOf(contractRevisionDetail.getSalesContractBase().getState().getState()) )")
    @Mapping(target = "tax", source = "vat")
    @Mapping(target = "untaxedTotal", source = "totalPrice")
    @Mapping(target = "taxedTotal", source = "totalPriceVat")
    @Mapping(target = "supplierContactName", source = "salerContactName")
    @Mapping(target = "supplierContactPhone", source = "salerContactPhone")
    @Mapping(target = "goodsVat", source = "vatProductRate")
    @Mapping(target = "serviceVat", source = "vatServiceRate")
    @Mapping(target = "areaCode", source = "areaCode")
    @Mapping(target = "areaName", source = "areaName")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "consigneeName", source = "consigneeName")
    @Mapping(target = "consigneePhone", source = "consigneePhone")
    @Mapping(target = "confirmTaxedTotal", source = "confirmTotalPriceVat")
    @Mapping(target = "discount", source = "discount")
    @Mapping(target = "discountedTotalPrice", source = "discountedTotalPrice")
    @Mapping(target = "revisions",source = "salesContractRevisions")
   TContract toTContractDetail(SalesContractRevisionDetail contractRevisionDetail);

    List<TRevision> toTContractDetails(List<SalesContractRevision> salesContractRevisions);
    @Mapping(target = "revision",source = "salesContractRevisionId.revision")
    TRevision toTRevision(SalesContractRevision salesContractRevision);


    SalesContractRevision toContractRevision(SalesContractRevisionDetail contractRevisionDetail);

    /**
     * 复制合同基础信息
     * @param contractBase 合同基础信息
     * @return 新的合同基础信息
     */
    @Mapping(target = "state",expression = "java( com.linzhi.gongfu.enumeration.ContractState.UN_FINISHED)")
    SalesContractBase toContractBase(SalesContractBase contractBase);
}
