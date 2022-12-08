package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Set;

/**
 * 用于响应前端外客户列表的请求
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VForeignCustomerPageResponse extends VBaseResponse {
    /**
     * 外客户列表
     */
    List<VForeignCustomer> customers;

    /**
     * 当前页码
     */
    private int current;

    /**
     * 总条数
     */
    private int total;

    @Data
    public static class VForeignCustomer {

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
         * 经营品牌名称
         */
        private List<VBrand> brands;

        /**
         * 授权操作员
         */
        private List<VOperator> operators;

    }

    @Data
    public static class VBrand {
        /**
         * 编码
         */
        private String code;

        /**
         * 名称
         */
        private String name;
    }

    @Data
    public static class VOperator {
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
