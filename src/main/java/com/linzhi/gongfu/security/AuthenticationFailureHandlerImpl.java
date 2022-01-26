package com.linzhi.gongfu.security;

import com.linzhi.gongfu.infrastructure.HttpServletJsonResponseWrapper;
import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理用户登录失败响应的处理器
 *
 * @author xutao
 * @create_at 2021-12-23
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.debug("操作员登录被拒绝：[{}] {}", exception.getClass().getSimpleName(), exception.getMessage());
        var failureResponse = VBaseResponse.builder()
            .code(403)
            .message("Access Denied")
            .build();
        HttpServletJsonResponseWrapper.wrap(response).write(200, failureResponse);
    }
}