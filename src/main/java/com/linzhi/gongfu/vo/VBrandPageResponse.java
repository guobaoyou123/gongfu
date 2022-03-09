package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于首页品牌的预加载请求
 *
 * @author zgh
 * @create_at 2022-02-07
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VBrandPageResponse extends  VBaseResponse{
    private int current;
    private int total;
    List<VBrandPageResponse.VBrand> brands;
    /**
     * 用于表示一个首页展示供应商品牌
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
         * 该品牌是否有品牌管理方
         */
        private Boolean haveOwned;
        /**
         * 当前公司是否是品牌管理方
         */
        private Boolean owned;
        /**
         * 当前公司是否正在营销此品牌
         */
        private Boolean  vending;
    }

}
