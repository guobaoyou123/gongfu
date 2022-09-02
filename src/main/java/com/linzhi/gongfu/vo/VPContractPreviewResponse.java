package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于响应前端对于修改采购合同预览展示请求
 *
 * @author zgh
 * @create_at 2022-06-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VPContractPreviewResponse extends VBaseResponse {

    /**
     * 产品列表
     */
    List<VProduct> products;

    @Data
    public static class VProduct {

        /**
         * 产品主键
         */
        private String productId;

        /**
         * 产品编码
         */
        private String productCode;

        /**
         * 数量
         */
        private BigDecimal amount;

        /**
         * 修改后数量
         */
        private BigDecimal modifiedAmount;

        /**
         * 已开票数量
         */
        private BigDecimal invoicedAmount;

        /**
         * 已收货数量
         */
        private BigDecimal receivedAmount;
    }
}
