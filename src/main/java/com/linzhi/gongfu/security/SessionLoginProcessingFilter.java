package com.linzhi.gongfu.security;

import com.linzhi.gongfu.security.token.OperatorAuthenticationToken;
import com.linzhi.gongfu.util.URLTools;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 用于解析用户请求中携带的Authorization头，并使用其完成登录的过滤器
 *
 * @author xutao
 * @create_at 2021-12-29
 */
@Component
public final class SessionLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    public SessionLoginProcessingFilter(AuthenticationFailureHandler failureHandler) {
        super(AnyRequestMatcher.INSTANCE);
        setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        var domainName = URLTools.extractSubdomainName(request.getHeader("Host"));
        var authorizationHeader = request.getHeader("Authorization");
        if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("来自用户的请求未提供合格的认证头信息。");
        }
        final var token = authorizationHeader.substring("Bearer ".length());
        var requestToken = new OperatorAuthenticationToken(domainName, token);
        return this.getAuthenticationManager().authenticate(requestToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        if (Objects.nonNull(this.eventPublisher)) {
            this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }
        chain.doFilter(request, response);
    }
}
