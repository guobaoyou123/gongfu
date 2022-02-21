package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Set;

/**
 * 用于前端查询供应商列表的响应体组建
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VSuppliersResponse extends VBaseResponse  {

    Set<VSupplier> suppliers;
    /**
     * 用于表示一个供应商
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
    }

}
