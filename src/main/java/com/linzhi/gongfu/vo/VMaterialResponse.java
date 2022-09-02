package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于主材质的预加载请求
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VMaterialResponse extends VBaseResponse {

    /**
     * 主材质列表
     */
    List<VMaterial> materials;

    @Data
    public static class VMaterial {

        /**
         * 编码
         */
        private String code;

        /**
         * 名称
         */
        private String name;

        /**
         * 子主材质列表
         */
        private List<VSubMaterial> children;
    }

    @Data
    public static class VSubMaterial {

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
