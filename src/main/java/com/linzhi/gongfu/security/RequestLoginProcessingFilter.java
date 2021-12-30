package com.linzhi.gongfu.security;

import com.linzhi.gongfu.security.token.OperatorLoginRequestToken;
import com.linzhi.gongfu.util.URLTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用于处理用户使用表单登录，并完成登录响应的过滤器
 *
 * @author xutao
 * @create_at 2021-12-29
 */
@Slf4j
public final class RequestLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_MATCHER = new AntPathRequestMatcher("/login", "POST");

    public RequestLoginProcessingFilter(
        AuthenticationSuccessHandler successHandler,
        AuthenticationFailureHandler failureHandler,
        AuthenticationManager authenticationManager
    ) {
        super(DEFAULT_LOGIN_PATH_MATCHER, authenticationManager);
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            var domainName = URLTools.extractSubdomainName(request.getHeader("Host"));
            var operatorCode = request.getParameter("code");
            var loginPassword = request.getParameter("password");
            log.debug("收到操作员登录请求：[{}@{}] - [{}]", operatorCode, domainName, loginPassword);
            var requestToken = new OperatorLoginRequestToken(operatorCode, domainName, loginPassword);
            return this.getAuthenticationManager().authenticate(requestToken);
        }
    }

}
