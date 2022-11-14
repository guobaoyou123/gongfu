package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.*;
import com.linzhi.gongfu.entity.InquiryRecordDetail;
import com.linzhi.gongfu.entity.NotificationInquiry;
import com.linzhi.gongfu.entity.NotificationInquiryRecord;
import com.linzhi.gongfu.vo.VOfferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换前端报价结构
 *
 * @author zgh
 * @create_at 2022-11-10
 */
@Mapper(componentModel = "spring", uses = {InquiryRecordMapper.class})
public interface NotificationInquiryMapper {

    /**
     * 将获取的报价产品转换成可供使用的报价产品明细
     * @param inquiryRecord 报价产品明细
     * @return 返回可供使用的报价产品明细
     */
    @Mapping(target = "code",source = "notificationInquiryRecordId.code")
    @Mapping(target = "productId",source = "productId")
    @Mapping(target = "productCode",source = "productCode")
    @Mapping(target = "productDescription",source = "productDescription")
    @Mapping(target = "isOffer",expression = "java(inquiryRecord.getIsOffer()!=null?String.valueOf(inquiryRecord.getIsOffer().getState()):null)")
    TInquiryRecord toInquiryProduct(NotificationInquiryRecord inquiryRecord);

    /**
     * 将获取的询价记录基础信息转换为可供使用的询价记录基础信息
     * @param inquiry 询价记录基础信息
     * @return 可供使用的询价记录基础信息
     */
    @Mapping(target = "code",source = "messageCode")
    @Mapping(target = "taxModel",expression = "java(String.valueOf(inquiry.getOfferMode().getTaxMode()))")
    @Mapping(target = "state",expression = "java(String.valueOf(inquiry.getState().getType()))")
    TNotificationInquiry toNotificationInquiry(NotificationInquiry inquiry);

    /**
     * 将获取的报价产品详情（包括上次报价和上次销售价格）转换成可供使用的报价产品明细
     * @param inquiryRecord 报价产品明细
     * @return 返回可供使用的报价产品明细
     */
    @Mapping(target = "code",source = "notificationInquiryRecordId.code")
    @Mapping(target = "productId",source = "productId")
    @Mapping(target = "productCode",source = "productCode")
    @Mapping(target = "productDescription",source = "productDescription")
    @Mapping(target = "isOffer",expression = "java(inquiryRecord.getIsOffer()!=null?String.valueOf(inquiryRecord.getIsOffer().getState()):null)")
    TInquiryRecord toInquiryProduct(InquiryRecordDetail inquiryRecord);


    VOfferResponse.VInquiry toVInquiry(TNotificationInquiry inquiry);
}
