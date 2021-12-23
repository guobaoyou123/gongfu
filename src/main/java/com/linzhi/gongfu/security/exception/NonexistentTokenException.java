package com.linzhi.gongfu.security.exception;

public class NonexistentTokenException extends Exception {
    public NonexistentTokenException() {
        super("请求令牌不存在");
    }
}
