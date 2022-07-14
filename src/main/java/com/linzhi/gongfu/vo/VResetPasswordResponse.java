package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于响应前端对于重置密码的请求
 *
 * @author zgh
 * @create_at 2022-07-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VResetPasswordResponse  extends VBaseResponse{

    private String password;
}
