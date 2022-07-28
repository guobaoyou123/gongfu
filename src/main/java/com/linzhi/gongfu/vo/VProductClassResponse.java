package com.linzhi.gongfu.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端查询所有产品分类的请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VProductClassResponse extends VBaseResponse{
    /**
     * 产品分类列表
     */
    List<VProductClass> classes;
    @Data
    public static class VProductClass{
        /**
         * 编码
         */
        private String code;

        /**
         * 名称
         */
        private String name;

        /**
         * 子分类
         */
        private List<VSubProductClass> children;
    }
    @Data
    public static class VSubProductClass{
        /**
         * 编码
         */
        private String code;

        /**
         * 名称
         */
        private String name;
    }
}
