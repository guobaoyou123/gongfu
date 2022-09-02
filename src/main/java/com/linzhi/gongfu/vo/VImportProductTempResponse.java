package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于导入产品详情展示请求
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VImportProductTempResponse extends VBaseResponse {

    /**
     * 是否可以确认
     * 用于前端是否可以点击保存
     */
    private boolean confirmable;

    /**
     * 询价单/合同编码
     */
    private String enCode;

    /**
     * 产品列表
     */
    private List<VProduct> products;


    @Data
    public static class VProduct {
        /**
         * 行号
         */
        private Integer itemNo;

        /**
         * 产品id
         */
        private String productId;

        /**
         * 产品编码
         */
        private String code;

        /**
         * 品牌列表
         */
        private List<VBrand> brand;

        /**
         * 价格
         */
        private String price;

        /*
         *数量
         */
        private String amount;

        /**
         * 错误信息messages
         */
        private List<String> messages;

        /**
         * 已被确认的品牌编码
         */
        private String confirmedBrand;

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
        private int sort;
    }
}
