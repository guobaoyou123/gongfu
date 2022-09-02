package com.linzhi.gongfu.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Set;

/**
 * 用于前端首页供应商列表的响应体组建
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VSuppliersPageResponse extends VBaseResponse {

    List<VSupplier> suppliers;
    /**
     * 当前页
     */
    private int current;
    /**
     * 总条数
     */
    private int total;

    /**
     * 用于表示一个首页展示供应商
     */
    @Data
    public static class VSupplier {
        /**
         * 公司编号
         */
        private String code;

        /**
         * 公司简称
         */
        private String name;
        /**
         * 排序
         */
        private Integer sort;

        /**
         * 自营品牌
         */
        @Singular
        private Set<VBrand> brands;
    }

    @Data
    public static class VBrand {
        /**
         * 品牌编号
         */
        private String code;

        /**
         * 品牌简称
         */
        private String name;

        /**
         * 排序
         */
        private Integer sort;
        /**
         * 当前公司是否是品牌管理方
         */
        private Boolean owned;
        /**
         * 当前公司是否正在营销此品牌
         */
        private Boolean vending;

    }
}
