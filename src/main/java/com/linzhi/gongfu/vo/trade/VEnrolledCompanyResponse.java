package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于前端入格单位详情或者始终拒绝名单中的格友详情的响应体组建
 *
 * @author zgh
 * @create_at 2022-07-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VEnrolledCompanyResponse extends VBaseResponse {

    private VCompany company;

    @Data
    public static class VCompany {
        /**
         * 编码
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
         * 详细地址
         */
        private String address;

        /**
         * 公司简介
         */
        private String introduction;

        /**
         * 是否是我的供应商
         */
        private Boolean isSupplier;

        /**
         * 是否是我的客户
         */
        private Boolean isCustomer;

        /**
         * 状态
         */
        private String state;

        /**
         * 备注
         */
        private String remark;

    }
}
