package com.linzhi.gongfu.security;

import com.linzhi.gongfu.infrastructure.HttpServletJsonResponseWrapper;
import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用于定义登录认证失败响应的入口点
 *
 * @author xutao
 * @create_at 2021-12-23
 */
@Slf4j
@Component
public class AuthenticationFailureEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.debug("用户请求被拒绝：[{}] {}", authException.getClass().getSimpleName(), authException.getMessage());
        var failureResponse = VBaseResponse.builder()
            .code(403)
            .message("Privileges required.")
            .build();
        HttpServletJsonResponseWrapper.wrap(response).write(403, failureResponse);
    }
}
