package com.linzhi.gongfu.entity;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户会话信息
 * @author xutao
 * @create_at 2021-12-22
 */
@Jacksonized
@Data
@With
@Builder
public class DSession implements Serializable {
    /**
     * 用户令牌
     */
    @NotNull
    @NotBlank
    private String token;

    /**
     * 操作员所属公司编号
     */
    @NotNull
    @NotBlank
    private String companyCode;

    /**
     * 操作员所属公司中文名称
     */
    @NotNull
    @NotBlank
    private String companyName;

    /**
     * 操作员编号
     */
    @NotNull
    @NotBlank
    private String operatorCode;

    /**
     * 操作员名称
     */
    @NotNull
    @NotBlank
    private String operatorName;

    /**
     * 操作员是否是管理员
     */
    @NotNull
    private boolean admin;

    /**
     * 操作员本次登录的过期时间
     */
    @NotNull
    @NotBlank
    @FutureOrPresent
    private LocalDateTime expriesAt;
}
