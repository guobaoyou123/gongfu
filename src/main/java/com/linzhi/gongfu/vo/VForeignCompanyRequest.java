package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于接前端添加、修改、停用启用外供应商或者客户信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VForeignCompanyRequest {

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
    private List<String> brands ;

    /**
     * 授权操作员以逗号隔开
     */
    private String operators ;

    /**
     * 停用供应商编码列表
     */
    private List<String> codes;
}
