package com.linzhi.gongfu.security.exception;

import org.springframework.security.core.AuthenticationException;

public class NoFoundPasswordException extends AuthenticationException {
    public NoFoundPasswordException() {
        super("需要重新设置密码");
    }
}
