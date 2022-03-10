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
public class VProductListResponse extends VBaseResponse{
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
         * 品牌名称
         */
        private String brandName;
        /**
         * 品牌编号
         */
        private String brandCode;
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
