package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TInquiryRecord;
import com.linzhi.gongfu.dto.TProduct;
import com.linzhi.gongfu.entity.NotificationInquiryRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换前端报价结构
 *
 * @author zgh
 * @create_at 2022-11-10
 */
@Mapper(componentModel = "spring")
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
}
