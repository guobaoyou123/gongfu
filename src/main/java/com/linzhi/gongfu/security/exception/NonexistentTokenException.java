package com.linzhi.gongfu.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 报告令牌不存在于令牌存储中的异常
 *
 * @author xutao
 * @create_at 2021-12-29
 */
public class NonexistentTokenException extends AuthenticationException {
    public NonexistentTokenException() {
        super("请求令牌不存在");
    }
}
