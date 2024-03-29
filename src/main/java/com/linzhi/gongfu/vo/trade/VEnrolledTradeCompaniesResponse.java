package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端内供应商或者内客户列表的请求
 *
 * @author zgh
 * @create_at 2022-08-18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VEnrolledTradeCompaniesResponse extends VBaseResponse {

    /**
     * 内供应商列表
     */
    List<VEnrolledTradeCompany> companies;
    /**
     * 当前页
     */
    private int current;
    /**
     * 总条数
     */
    private int total;

    @Data
    public static class VEnrolledTradeCompany {

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
