package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 优选供应商列表的响应体
 *
 * @author zgh
 * @create_at 2022-11-04
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VPreferenceSupplierResponse extends VBaseResponse {
    /**
     * 品牌列表
     */
    List<VBrand> brands;

    /**
     * 用于表示一个经营品牌
     */
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
        private int sort;

        /**
         * 优选供应商列表
         */
        List<VSupplier> suppliers;
    }

    /**
     * 用于表示一个供应商
     */
    @Data
    public static class VSupplier{
        /**
         * 编码
         */
        private String code;

        /**
         * 页面展示编号
         */
        private String encode;

        /**
         * 供应商名称
         */
        private String name;

        /**
         * 排序
         */
        private int sort;
    }
}
