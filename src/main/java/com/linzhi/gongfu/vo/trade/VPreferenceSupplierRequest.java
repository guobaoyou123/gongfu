package com.linzhi.gongfu.vo.trade;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

/**
 * 用于接前端设置优选供应商的请求参数
 *
 * @author zgh
 * @create_at 2022-11-04
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VPreferenceSupplierRequest implements Serializable {
    /**
     * 品牌编号
     */
    private String brand;

    /**
     * 优选供应商编码
     */
    List<String> suppliers;

    /**
     * 排序供应商列表
     */
    List<VSupplier> sorts;

    @Data
    public static class VSupplier{
        /**
         * 供应商编码
         */
        private String code;

        /**
         * 排序
         */
        private int sort;
    }

}
