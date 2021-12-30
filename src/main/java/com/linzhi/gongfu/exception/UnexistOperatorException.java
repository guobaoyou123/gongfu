package com.linzhi.gongfu.exception;

public class UnexistOperatorException extends Exception {
    public UnexistOperatorException() {
        super("请求或查询了不存在的操作员。");
    }
}
