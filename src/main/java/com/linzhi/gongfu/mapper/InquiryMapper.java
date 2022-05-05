package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TInquiry;
import com.linzhi.gongfu.entity.InquiryDetail;
import com.linzhi.gongfu.entity.Inquiry;
import com.linzhi.gongfu.vo.VInquiryDetailResponse;
import com.linzhi.gongfu.vo.VInquiryListResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = { InquiryRecordMapper.class })
public interface InquiryMapper {
    /**
     * 转换询价单列表
     * @param inquiry 询价单基本信息
     * @return 询价单基本信息
     */
    @Mapping(target = "createdAt",expression ="java(com.linzhi.gongfu.util.DateConverter.dateFormat(inquiry.getCreatedAt()))" )
    @Mapping(target = "type",expression = "java(String.valueOf(inquiry.getType().getType()))")
    @Mapping(target = "state",expression = "java(String.valueOf(inquiry.getState().getState()))")
    TInquiry toInquiryList(Inquiry inquiry);
    /**
     * 转换询价单列表
     * @param tInquiry 询价单基本信息
     * @return 询价单基本信息
     */
    @Mapping(target = "salesContractCode",source = "salesOrderCode")
    @Mapping(target = "supplierName",source = "salerCompName")
    @Mapping(target = "ownerCode",source = "createdBy")
    @Mapping(target = "ownerName",source = "buyerContactName")
    VInquiryListResponse.VInquiry toVInquiryList(TInquiry tInquiry);

    /**
     * 转换询价单详情
     * @param inquiry 询价单基本信息
     * @return 询价单详情基本信息
     */
    @Mapping(target = "createdAt",expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(inquiry.getCreatedAt()))")
    @Mapping(target = "deletedAt",expression = "java(inquiry.getDeletedAt()!=null?com.linzhi.gongfu.util.DateConverter.dateFormat(inquiry.getDeletedAt()):null)")
    @Mapping(target = "confirmedAt",expression = "java(inquiry.getConfirmedAt()!=null?com.linzhi.gongfu.util.DateConverter.dateFormat(inquiry.getConfirmedAt()):null)")
    @Mapping(target = "state",expression = "java(String.valueOf(inquiry.getState().getState()))")
    @Mapping(target = "offerMode",expression = "java(String.valueOf(inquiry.getOfferMode().getTaxMode()))")
    @Mapping(target = "vat",expression = "java(com.linzhi.gongfu.service.InquiryService.judgeInquiryMoney(inquiry.getVat(),inquiry.getRecords()))")
    @Mapping(target = "totalPrice",expression = "java(com.linzhi.gongfu.service.InquiryService.judgeInquiryMoney(inquiry.getTotalPrice(),inquiry.getRecords()))")
    @Mapping(target = "totalPriceVat",expression = "java(com.linzhi.gongfu.service.InquiryService.judgeInquiryMoney(inquiry.getTotalPriceVat(),inquiry.getRecords()))")
    TInquiry toInquiryDetail(InquiryDetail inquiry);


    /**
     * 转换询价单详情
     * @param tInquiry 询价单基本信息
     * @return 询价单详情基本信息
     */
    @Mapping(target = "contractNo",source = "orderCode")
    @Mapping(target = "supplierNo",source = "salerOrderCode")
    @Mapping(target = "salesContractCode",source = "salesOrderCode")
    @Mapping(target = "supplierCode",source = "salerComp")
    @Mapping(target = "supplierName",source = "salerCompName")
    @Mapping(target = "supplierContactName",source = "salerContactName")
    @Mapping(target = "supplierContactPhone",source = "salerContactPhone")
    @Mapping(target = "goodsVat",source = "vatProductRate")
    @Mapping(target = "serviceVat",source = "vatServiceRate")
    @Mapping(target = "tax",source = "vat")
    @Mapping(target = "untaxedTotal",source = "totalPrice")
    @Mapping(target = "taxedTotal",source = "totalPriceVat")
    @Mapping(target = "confirmTaxedTotal",source = "confirmTotalPriceVat")
    @Mapping(target = "products",source = "records")
    @Mapping(target = "ownerCode",source = "createdBy")
    @Mapping(target = "ownerName",source = "buyerContactName")
   VInquiryDetailResponse.VInquiry toVInquiryDetail(TInquiry tInquiry);
}
