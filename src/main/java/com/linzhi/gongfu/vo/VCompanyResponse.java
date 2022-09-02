package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于前端本单位信息请求的响应体组建
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VCompanyResponse extends VBaseResponse {

    private VCompany company;

    @Data
    public static class VCompany {

        /**
         * 公司编码
         */
        private String code;

        /**
         * 外供应商编码
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
         * 联系人姓名
         */
        private String contactName;

        /**
         * 联系人电话
         */
        private String contactPhone;

        /**
         * 区域编码
         */
        private String areaCode;

        /**
         * 区域名称
         */
        private String areaName;

        /**
         * 地址
         */
        private String address;

        /**
         * 公司简介
         */
        private String introduction;

        /**
         * 是否对格友可见
         */
        private Boolean visible;

        /**
         * 可见内容
         */
        private String content;
    }

}
