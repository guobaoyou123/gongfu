package com.linzhi.gongfu.security;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.linzhi.gongfu.infrastructure.HttpServletJsonResponseWrapper;
import com.linzhi.gongfu.util.URLTools;
import com.linzhi.gongfu.vo.VBaseResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于处理操作员成功完成登出系统的响应的处理器
 *
 * @author xutao
 * @create_at 2021-12-23
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    private final TokenStore tokenStore;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        try {
            String tokenHeader = request.getHeader("Authorization");
            String requestDomain = URLTools.extractSubdomainName(request.getHeader("Host"));
            if (Objects.nonNull(tokenHeader) && tokenHeader.startsWith("Bearer ")) {
                final String token = tokenHeader.substring("Bearer ".length());
                tokenStore.revoke(requestDomain, token);
            }
            var successResponse = VBaseResponse.builder().code(200).message("Success").build();
            HttpServletJsonResponseWrapper.wrap(response).write(200, successResponse);
        } catch (IllegalArgumentException e) {
            log.info("未能从用户请求中提取到用于鉴别用户会话的有效信息: {}", e.getMessage());
        }
    }
}
