package com.linzhi.gongfu.mapper.trade;

import com.linzhi.gongfu.dto.TInquiryRecord;
import com.linzhi.gongfu.entity.InquiryRecord;
import com.linzhi.gongfu.entity.NotificationInquiryRecord;
import com.linzhi.gongfu.vo.trade.VInquiryDetailResponse;
import com.linzhi.gongfu.vo.trade.VNotificationResponse;
import com.linzhi.gongfu.vo.trade.VOfferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {InquiryRecordMapper.class})
public interface InquiryRecordMapper {
    List<TInquiryRecord> toTInquiryRecordDos(List<InquiryRecord> records);

    @Mapping(target = "inquiryId", source = "inquiryRecordId.inquiryId")
    @Mapping(target = "code", source = "inquiryRecordId.code")
    @Mapping(target = "type", expression = "java(String.valueOf(inquiryRecord.getType().getType()))")
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(inquiryRecord.getCreatedAt()))")
    @Mapping(target = "amount", expression = "java(inquiryRecord.getAmount()!=null?inquiryRecord.getAmount().setScale(2):null)")
    @Mapping(target = "totalPrice", expression = "java(inquiryRecord.getTotalPrice()!=null?inquiryRecord.getTotalPrice().setScale(2):null)")
    @Mapping(target = "totalPriceVat", expression = "java(inquiryRecord.getTotalPriceVat()!=null?inquiryRecord.getTotalPriceVat().setScale(2):null)")
    @Mapping(target = "totalDiscountedPrice", expression = "java(inquiryRecord.getTotalDiscountedPrice()!=null?inquiryRecord.getTotalDiscountedPrice().setScale(2):null)")
    @Mapping(target = "totalDiscountedPriceVat", expression = "java(inquiryRecord.getTotalDiscountedPriceVat()!=null?inquiryRecord.getTotalDiscountedPriceVat().setScale(2):null)")
    TInquiryRecord toTInquiryRecordDo(InquiryRecord inquiryRecord);

    List<VInquiryDetailResponse.VProduct> toVProductDos(List<TInquiryRecord> records);

    @Mapping(target = "itemNo", source = "code")
    @Mapping(target = "id", source = "productId")
    @Mapping(target = "code", source = "productCode")
    @Mapping(target = "describe", source = "productDescription")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "brandCode", source = "brandCode")
    @Mapping(target = "brandName", source = "brand")
    @Mapping(target = "vatRate", source = "vatRate")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "priceVat", source = "priceVat")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "afterDiscountPrice", source = "discountedPrice")
    @Mapping(target = "totalAfterDiscountPrice", source = "totalDiscountedPrice")
    @Mapping(target = "afterDiscountPriceVat", source = "discountedPriceVat")
    @Mapping(target = "totalAfterDiscountPriceVat", source = "totalDiscountedPriceVat")
    VInquiryDetailResponse.VProduct toVProductDo(TInquiryRecord record);


    /**
     * 将询价记录装换成报价记录
     * @param inquiryRecord 询价记录
     * @return 返回报价记录
     */
    @Mapping(target = "notificationInquiryRecordId.code",source = "inquiryRecordId.code")
    @Mapping(target = "price", ignore = true)
    NotificationInquiryRecord toNotificationInquiryRecord(InquiryRecord inquiryRecord);

    /**
     * 将获取的可供使用的报价明细列表装换成页面展示的报价明细列表
     * @param products 报价明细列表
     * @return 前端展示的报价明细列表
     */
    List<VNotificationResponse.VProduct> totoVNotificationRecodeDetails(List<TInquiryRecord> products);

    /**
     * 将获取的可供使用的报价明细装换成页面展示的报价明细
     * @param record 报价明细
     * @return 前端展示的报价明细
     */
    @Mapping(target = "itemNo",source = "code")
    @Mapping(target = "id",source = "productId")
    @Mapping(target = "code",source = "productCode")
    @Mapping(target = "describe",source = "productDescription")
    @Mapping(target = "brandName",source = "brand")
    @Mapping(target = "isOffer",expression = "java(record.getIsOffer()==null?true:record.getIsOffer().equals(\"1\")?true:false)")
    VNotificationResponse.VProduct totoVNotificationRecodeDetail(TInquiryRecord record);

    /**
     * 将获取的可供使用的报价明细(包括上次报价价格和上次销售价格)装换成页面展示的报价明细
     * @param record 报价明细
     * @return 前端展示的报价明细(包括上次报价价格和上次销售价格)
     */
    @Mapping(target = "itemNo",source = "code")
    @Mapping(target = "id",source = "productId")
    @Mapping(target = "code",source = "productCode")
    @Mapping(target = "describe",source = "productDescription")
    @Mapping(target = "brandName",source = "brand")
    @Mapping(target = "isOffer",expression = "java(record.getIsOffer()==null?true:record.getIsOffer().equals(\"1\")?true:false)")
    VOfferResponse.VProduct toVOfferDetail(TInquiryRecord record);
}
