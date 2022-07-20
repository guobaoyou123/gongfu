package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于前端入格单位详情的响应体组建
 *
 * @author zgh
 * @create_at 2022-07-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VEnrolledCompanyDetailResponse extends VBaseResponse{

    private VCompany company;

    @Data
    public static class VCompany{
        private String code;

        private String companyName;

        private String companyShortName;

        private String usci;

        private String contactName;

        private String contactPhone;

        private String areaCode;

        private String areaName;

        private String address;

        private String introduction;

        private Boolean isSupplier;

        private Boolean isCustomer;

        private String state;

        private String remark;

    }
}
