package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;
/**
 * 用于响应前端对于查询产品列表的预加载请求
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VProductPageResponse extends VBaseResponse{

    /**
     * 当前页
     */
    private int current;

    /**
     * 总条数
     */
    private int total;

    /**
     * 产品列表
     */
    List<VProduct> products;

    /**
     * 其他相关产品列表
     */
    List<VProduct> otherproducts;
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
         * 产品描述
         */
        private String describe;

        /**
         * 计价单位
         */
        private String chargeUnit;
        /**
         * 产品面价
         */
        private BigDecimal facePrice;
    }
}
