package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于前端供应商详情的响应体组建
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VCompanyDetailResponse extends VBaseResponse  {

    private VCompany company;
    @Data
    public  static  class  VCompany{
        private String code;

        private String encode;

        private String companyName;

        private String companyShortName;

        private String usci;

        private String contactName;

        private String contactPhone;

        private String areaCode;

        private String areaName;

        private String address;

        private String introduction;

        /**
         * 是否对格友可见
         */
        private Boolean  visible;

        /**
         * 可见内容
         */
        private String content;
    }

}
