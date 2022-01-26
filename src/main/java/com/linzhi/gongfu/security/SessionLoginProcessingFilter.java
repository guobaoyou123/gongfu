package com.linzhi.gongfu.security;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.linzhi.gongfu.security.token.OperatorAuthenticationToken;
import com.linzhi.gongfu.util.URLTools;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

/**
 * 用于解析用户请求中携带的Authorization头，并使用其完成登录的过滤器
 *
 * @author xutao
 * @create_at 2021-12-29
 */
public final class SessionLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    public SessionLoginProcessingFilter(AuthenticationFailureHandler failureHandler,
            AuthenticationManager authenticationManager) {
        super(AnyRequestMatcher.INSTANCE, authenticationManager);
        setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        Optional<String> domainName = Optional.ofNullable(request.getHeader("CompanyDomain"))
                .or(() -> Optional.ofNullable(request.getParameter("host")))
                .map(URLTools::extractSubdomainName);
        final var token = domainName.isPresent() ? obtainToken(request, URLTools.isRunningLocally(domainName.get()))
                : null;
        var requestToken = new OperatorAuthenticationToken(domainName.orElse(null), token);
        return this.getAuthenticationManager().authenticate(requestToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        if (Objects.nonNull(this.eventPublisher)) {
            this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }
        chain.doFilter(request, response);
    }

    /**
     * 从用户HTTP请求中分离用户会话令牌。
     * <p>
     * 如果是使用本地模式，那么令牌将从请求参数中的{@code token}参数中获取，否则从请求头{@code token}中获取。
     * </p>
     *
     * @param request 用户HTTP请求
     * @param locally 是否采用本地提取模式
     * @return 从用户请求中提取的用户会话令牌
     * @throws BadCredentialsException 当用户请求中不能分离得到令牌或者令牌不符合标准格式时抛出
     */
    private String obtainToken(HttpServletRequest request, boolean locally) throws BadCredentialsException {
        var authorization = locally ? request.getParameter("token") : request.getHeader("Authorization");
        if (!locally) {
            if (Objects.isNull(authorization) || !authorization.startsWith("Bearer ")) {
                // throw new BadCredentialsException("来自用户的请求未提供合格的认证头信息。");
                return null;
            }
            authorization = authorization.substring("Bearer ".length());
        }
        return authorization;
    }
}
