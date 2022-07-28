package com.linzhi.gongfu.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Whether;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

/**
 * 用于转移操作员基本信息
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TOperatorInfo implements Serializable {

    /**
     * 公司编码
     */
    private String companyCode;

    /**
     * 操作员编码
     */
    private String code;
    /**
     * 姓名
     */
    private String name;
    /**
     * 密码
     */
    private String password;
    /**
     * 状态（0-禁用 1-启用）
     */
    private Availability state;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 公司简称
     */
    private String companyShortName;
    /**
     * 域名
     */
    private String companyDomain;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 是否为管理员
     */
    private Whether admin;
    /**
     * 对应廪实编码
     */
    private String LSCode;
    /**
     * 场景列表
     */
    private Set<TScene> scenes;
    /**
     * 出生日期（YYYY-MMM-DD）
     */
    private String birthday;

    /**
     * 性别
     */
    private String  sex;

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
     * 入职时间
     */
    private String entryAt;
    /**
     * 离职时间
     */
    private String resignationAt;
    /**
     * 是否已修改密码
     */
    private Whether changed;
}
