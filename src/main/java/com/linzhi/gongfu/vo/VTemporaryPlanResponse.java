package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VTemporaryPlanResponse extends VBaseResponse{
    List<VProduct> products;
    @Data
    public static class VProduct{
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
    }
}
