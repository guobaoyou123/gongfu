package com.linzhi.gongfu.mapper.trade;


import com.linzhi.gongfu.dto.TContract;
import com.linzhi.gongfu.dto.TContractReceived;
import com.linzhi.gongfu.dto.TRevision;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.vo.trade.VDeliveredResponse;
import com.linzhi.gongfu.vo.trade.VPContractDetailResponse;
import com.linzhi.gongfu.vo.trade.VPContractPageResponse;
import com.linzhi.gongfu.vo.trade.VReceivedResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 用于转换合同相关信息
 *
 * @author xutao
 * @create_at 2022-05-24
 */
@Mapper(componentModel = "spring", uses = {ContractRecordMapper.class})
public interface ContractMapper {


    /**
     * 将获取到的采购合同列表详情转换成可供使用的采购合同列表详情
     *
     * @param contract 采购合同基本信息
     * @return 采购合同基本信息
     */
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(contract.getCreatedAt()))")
    @Mapping(target = "state", expression = "java(String.valueOf(contract.getState().getState()))")
    @Mapping(target = "salerCompName", source = "salerCompNameShort")
    @Mapping(target = "paired", expression = "java(contract.getPaired()==null?false:true)")
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
    @Mapping(target = "customerContractNo",source = "customerContractNo")
    VPContractPageResponse.VContract toContractPage(TContract tContract);

    /**
     * 将获取到的采购合同版本详情转换成可供使用的采购合同版本详情
     *
     * @param contractRevisionDetail 采购合同版本详情
     * @return 采购合同版本详情
     */
    @Mapping(target = "id", source = "purchaseContractRevisionId.id")
    @Mapping(target = "revision", source = "purchaseContractRevisionId.revision")
    @Mapping(target = "code", source = "purchaseContractBase.code")
    @Mapping(target = "orderCode", source = "orderCode")
    @Mapping(target = "supplierContractNo", source = "salerOrderCode")
    @Mapping(target = "salesContractId", source = "purchaseContractBase.salesContractId")
    @Mapping(target = "salesContractCode",source = "purchaseContractBase.salesContractBase.code")
    @Mapping(target = "createdByComp", source = "purchaseContractBase.createdByComp")
    @Mapping(target = "createdBy", source = "purchaseContractBase.createdBy")
    @Mapping(target = "buyerComp", source = "purchaseContractBase.buyerComp")
    @Mapping(target = "buyerCompName", source = "purchaseContractBase.buyerCompName")
    @Mapping(target = "salerComp", source = "purchaseContractBase.salerComp")
    @Mapping(target = "salerCompName", source = "purchaseContractBase.salerCompName")
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(contractRevisionDetail.getCreatedAt()))")
    @Mapping(target = "offerMode", expression = "java(String.valueOf(contractRevisionDetail.getOfferMode().getTaxMode()))")
    @Mapping(target = "tax", source = "vat")
    @Mapping(target = "untaxedTotal", source = "totalPrice")
    @Mapping(target = "taxedTotal", source = "totalPriceVat")
    @Mapping(target = "state", expression = "java( String.valueOf(contractRevisionDetail.getPurchaseContractBase().getState().getState()) )")
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
    @Mapping(target = "revisions", source = "purchaseContractRevisions")
    TContract toTContractDetail(PurchaseContractRevisionDetail contractRevisionDetail);

    /**
     * 将获取到的合同版本列表 转化为可供使用的合同版本列表
     * @param purchaseContractRevisions 采购合同版本列表
     * @return 可供使用的采购合同版本列表
     */
    List<TRevision> toTContractRevisions(List<PurchaseContractRevision> purchaseContractRevisions);

    /**
     * 获取版本号和时间
     * @param purchaseContractRevision 采购合同版本详情
     * @return 仅带有版本号和时间的合同版本详情
     */
    @Mapping(target = "revision", source = "purchaseContractRevisionId.revision")
    TRevision toTContractRevision(PurchaseContractRevision purchaseContractRevision);

    /**
     * 将可供使用的采购合同详情转换为前台展示的采购合同详情
     *
     * @param tContract 可供使用的采购合同详情
     * @return 前台展示的采购合同详情
     */
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

    /**
     * 从合同详情中提取合同版本基础信息
     *
     * @param contractRevisionDetail 合同详情
     * @return 合同版本基础信息
     */
    PurchaseContractRevision toContractRevision(PurchaseContractRevisionDetail contractRevisionDetail);

    /**
     * 采购合同收货列表
     *
     * @param contractReceived 收发货记录
     * @return 收货列表
     */
    @Mapping(target = "received", expression = "java(contractReceived.getReceived()!=null?contractReceived.getDelivered()!=null?contractReceived.getReceived().subtract(contractReceived.getDelivered()):contractReceived.getReceived():null)")
    TContractReceived toTContractReceived(ContractReceived contractReceived);

    /**
     * 收货记录
     *
     * @param tContractReceived 收货记录
     * @return 收货记录
     */
    @Mapping(target = "receivedAmount", source = "received")
    VReceivedResponse.VProduct toVProduct(TContractReceived tContractReceived);

    /**
     * 发货记录
     *
     * @param tContractReceived 发货记录
     * @return 发货记录
     */
    @Mapping(target = "deliveredAmount", source = "delivered")
    VDeliveredResponse.VProduct toVDeliveredProduct(TContractReceived tContractReceived);

    /**
     * 将获取到的销售合同列表转换成可供使用的销售合同列表
     *
     * @param contract 销售合同基本信息
     * @return 销售合同基本信息
     */
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(contract.getCreatedAt()))")
    @Mapping(target = "buyerComp", source = "buyerCompCode")
    @Mapping(target = "buyerCompName", source = "buyerCompName")
    @Mapping(target = "state", expression = "java(String.valueOf(contract.getState().getState()))")
    @Mapping(target = "paired", expression = "java(contract.getPaired()==null?false:true)")
    @Mapping(target = "customerContractNo",source = "customerContractNo")
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
    @Mapping(target = "revisions", source = "salesContractRevisions")
    TContract toTContractDetail(SalesContractRevisionDetail contractRevisionDetail);

    /**
     * 获取合同版本列表
     * @param salesContractRevisions 合同版本详情列表
     * @return 合同版本列表
     */
    List<TRevision> toTContractDetails(List<SalesContractRevision> salesContractRevisions);

    /**
     * 获取版本号和创建时间
     * @param salesContractRevision  合同版本详情
     * @return 仅带有版本号和创建时间的合同版本详情
     */
    @Mapping(target = "revision", source = "salesContractRevisionId.revision")
    TRevision toTRevision(SalesContractRevision salesContractRevision);

    /**
     * 获取合同版本详情
     * @param contractRevisionDetail 合同详情
     * @return 合同版本详情
     */
    SalesContractRevision toContractRevision(SalesContractRevisionDetail contractRevisionDetail);

    /**
     * 复制合同基础信息
     *
     * @param contractBase 合同基础信息
     * @return 新的合同基础信息
     */
    @Mapping(target = "state", expression = "java( com.linzhi.gongfu.enumeration.ContractState.UN_FINISHED)")
    SalesContractBase toContractBase(SalesContractBase contractBase);


    /**
     * 销售合同发货记录
     *
     * @param contractReceived 收发货记录
     * @return 销售合同发货记录
     */
    @Mapping(target = "delivered", expression = "java(contractReceived.getDelivered()!=null?contractReceived.getReceived()!=null?contractReceived.getDelivered().subtract(contractReceived.getReceived()):contractReceived.getDelivered():null)")
    TContractReceived toTContractDelivered(ContractReceived contractReceived);
}
