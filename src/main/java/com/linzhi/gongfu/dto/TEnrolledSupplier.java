package com.linzhi.gongfu.dto;


import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TEnrolledSupplier {

    /**
     * 入格供应商编码
     */
    private String code;

    /**
     * 入格供应商公司名称
     */
    private String companyName;

    /**
     * 入格供应商公司简称
     */
    private String companyShortName;

    /**
     * 社会统一信用代码
     */
    private String usci;

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
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 供应商对外可见内容
     */
    private String visibleContent;

    /**
     * 公司简介
     */
    private String  introduction;

    /**
     * 税模式
     */
    private String taxMode;

    /**
     * 品牌列表
     */
    private List<TBrand> brands;

    /**
     * 操作员列表
     */
    private List<TOperatorInfo> operators;

}
