package com.linzhi.gongfu.vo.trade;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于接前端修改询价单或者采购或则销售合同信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VInquiryRequest {

    /**
     * 税模式
     */
    private String taxModel;

    /**
     * 货物税率
     */
    private BigDecimal goodsVat;

    /**
     * 服务税率
     */
    private BigDecimal serviceVat;

    /**
     * 产品列表
     */
    private List<VProduct> products;

    @Data
    public static class VProduct {

        /**
         * 服务列表(销售合同用)
         */
        List<VService> services;
        /**
         * 产品序号
         */
        private int code;
        /**
         * 价格
         */
        private BigDecimal price;
        /**
         * 税率
         */
        private BigDecimal vatRate;
        /**
         * 数量
         */
        private BigDecimal amount;
        /**
         * 计价单位(销售合同用)
         */
        private String chargeUnit;
        /**
         * 与系统计价单位的比例(销售合同用)
         */
        private BigDecimal ratio;
    }

    @Data
    public static class VService {

        /**
         * 产品序号
         */
        private int code;

        /**
         * 价格
         */
        private BigDecimal price;

        /**
         * 税率
         */
        private BigDecimal vatRate;

        /**
         * 数量
         */
        private BigDecimal amount;

        /**
         * 服务备注(销售合同用)
         */
        private String remark;

    }
}
