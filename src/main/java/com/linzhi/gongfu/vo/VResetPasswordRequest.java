package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * 用于接前端重置密码的请求参数
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VResetPasswordRequest implements Serializable {
    /**
     * 操作员编码
     */
    private String code;

    /**
     * 新密码
     */
    private String password;

    /**
     * 旧密码
     */
    private String oldpassword;
}
