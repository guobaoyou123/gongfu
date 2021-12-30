package com.linzhi.gongfu.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 报告没有在系统中找到指定操作员的异常
 *
 * @author xutao
 * @create_at 2021-12-29
 */
public class OperatorNotFoundException extends AuthenticationException {
    public OperatorNotFoundException() {
        super("未找到指定的操作员。");
    }
}
