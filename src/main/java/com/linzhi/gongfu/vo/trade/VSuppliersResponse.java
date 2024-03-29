package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

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
public class VSuppliersResponse extends VBaseResponse {

    List<VSupplier> suppliers;

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
        /**
         * 排序
         */
        private Integer sort;
    }

}
