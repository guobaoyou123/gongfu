package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
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
public class VUnfinishedInquiryListResponse extends VBaseResponse {

    /**
     * 未完成的询价单列表
     */
    List<VInquiry> inquiries;

    @Data
    public static class VInquiry {
        /**
         * 询价单主键
         */
        private String id;
        /**
         * 询价单编码
         */
        private String code;
        /**
         * 预计价税合计
         */
        private BigDecimal taxedTotal;

        /**
         * 种类
         */
        private String category;
    }
}
