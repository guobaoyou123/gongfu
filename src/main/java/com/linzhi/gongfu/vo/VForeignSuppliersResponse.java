package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于驱动方式的预加载请求
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VForeignSuppliersResponse extends VBaseResponse{

    List<VForeignSupplier> suppliers;

    @Data
    public static class VForeignSupplier{

        /**
         * 系统编码
         */
        private String code;

        /**
         * 编码
         */
        private String encode;

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

        /**
         * 状态
         */
        private String state;

    }
}
