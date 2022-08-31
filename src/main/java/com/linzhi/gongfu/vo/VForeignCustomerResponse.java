package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于前端外客户情的响应体组建
 *
 * @author zgh
 * @create_at 2022-08-31
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VForeignCustomerResponse extends VBaseResponse  {

    private VCustomer customer;
    @Data
    public  static  class  VCustomer{
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
         * 联系人
         */
        private String contactName;

        /**
         * 联系电话
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
         * 电子邮箱
         */
        private String email;

        /**
         * 公司电话
         */
        private String phone;

        /**
         * 税模式
         */
        private String taxMode;

        /**
         * 品牌列表
         */
        private List<VBrand> brands ;

        /**
         * 操作员列表
         */
        private List<VOperator> operators ;
    }

    @Data
    public  static  class  VBrand{
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
    public  static  class  VOperator{
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
