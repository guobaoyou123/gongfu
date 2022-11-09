package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TInquiryRecord;
import com.linzhi.gongfu.entity.InquiryRecord;
import com.linzhi.gongfu.entity.NotificationInquiryRecord;
import com.linzhi.gongfu.vo.VInquiryDetailResponse;
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
    NotificationInquiryRecord toNotificationInquiryRecord(InquiryRecord inquiryRecord);

}
