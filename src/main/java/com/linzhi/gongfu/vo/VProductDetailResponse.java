package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VProductDetailResponse extends VBaseResponse{

    VProduct product;
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
         * 品牌编码
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
    }
}
