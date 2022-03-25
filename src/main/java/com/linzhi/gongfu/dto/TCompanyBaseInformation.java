package com.linzhi.gongfu.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

/**
 * 用于转移公司基本信息
 *
 * @author xutao
 * @create_at 2022-01-19
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TCompanyBaseInformation implements Serializable {
    /**
     * 公司编号
     */
    private String code;

    private String encode;
    /**
     * 公司全名
     */
    private String name;

    /**
     * 公司简称
     */
    private String shortName;

    /**
     * 公司所使用的二级域名
     */
    private String subdomain;
    /**
     * 社会统一信用代码
     */
    private String USCI;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 系统区域编码
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
     * 公司邮箱
     */
    private String email;
    /**
     * 公司电话
     */
    private String phone;
    /**
     * 公司所具备的应用场景
     */
    @Singular
    private List<String> scenes;

    private String state;
}
