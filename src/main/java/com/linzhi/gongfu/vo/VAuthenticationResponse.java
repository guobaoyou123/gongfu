package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

/**
 * 用于响应用户登录成功后的响应体组建
 * @author xutao
 * @create_at 2021-12-24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VAuthenticationResponse extends VBaseResponse {
    /**
     * 操作员编号
     */
    private String operatorCode;

    /**
     * 操作员名称
     */
    private String operatorName;

    /**
     * 操作员所属公司编号
     */
    private String companyCode;

    /**
     * 操作员所属公司全称
     */
    private String companyName;

    /**
     * 操作员所属公司简称
     */
    private String companyShortName;

    /**
     * 操作员是否是公司管理员
     */
    private Boolean admin;

    /**
     * 登录有效期，此为截止到的时间
     */
    private LocalDateTime expiresAt;

    /**
     * 操作员登录Token，需要使用Authorization头传回
     */
    private String token;
}
