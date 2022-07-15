package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Data
@Jacksonized
@NoArgsConstructor
public class VResetPasswordRequest implements Serializable {
    private String code;
    private String password;
    private String oldpassword;
}
