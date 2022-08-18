package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端内供应商列表的请求
 *
 * @author zgh
 * @create_at 2022-08-18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VEnrolledSuppliersResponse extends VBaseResponse{

    /**
     * 当前页
     */
    private int current;
    /**
     * 总条数
     */
    private int total;

    /**
     * 内供应商列表
     */
    List<VEnrolledSupplier> suppliers;

    @Data
    public static class VEnrolledSupplier{

        /**
         * 系统编码
         */
        private String code;

        /**
         * 公司名称
         */
        private String companyName;

        /**
         * 公司简称
         */
        private String companyShortName;

        /**
         * 社会统一信用代码
         */
        private String usci;

    }
}
