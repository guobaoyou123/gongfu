package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;
/**
 * 用于响应查询未完成的询价单列表的请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VUnfinishedInquiryListResponse  extends VBaseResponse{

    List<VInquiry> inquiries;
    @Data
    public static class VInquiry{
        private String id;
        private String code;
        private BigDecimal taxedTotal;
        private String category;
    }
}
