package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于响应根据操作员编码、单位id查询该操作员的临时计划表的请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VTemporaryPlanResponse extends VBaseResponse {
    List<VProduct> products;

    @Data
    public static class VProduct {
        /**
         * 产品唯一码
         */
        private String id;
        /**
         * 产品编码
         */
        private String code;
        /**
         * 品牌代码
         */
        private String brandCode;
        /**
         * 品牌名称
         */
        private String brandName;

        /**
         * 产品描述
         */
        private String describe;
        /**
         * 计价单位
         */
        private String chargeUnit;
        /**
         * 需求
         */
        private BigDecimal demand;
        /**
         * 面价
         */
        private BigDecimal facePrice;
        /**
         * 创建时间
         */
        private Long createdAt;
    }
}
