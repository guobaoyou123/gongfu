package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

/**
 * 用于响应前端对于产品品牌的预加载请求
 *
 * @author zgh
 * @create_at 2022-02-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VDcBrandResponse extends VBaseResponse {
    Set<VBrand> brands;

    /**
     * 用于表示一个品牌
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

    }

}
