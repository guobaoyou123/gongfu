package com.linzhi.gongfu.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import lombok.RequiredArgsConstructor;

/**
 * 用于在应用中启动Spring Security，并配置各个功能的入口
 *
 * @author xutao
 * @create_at 2021-12-24
 */
@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final LogoutSuccessHandler logoutHandler;
    private final AuthenticationFailureEntryPoint failureEntryPoint;
    private final LoginRequestTokenAuthenticationProvider loginTokenProvider;
    private final SessionRequestTokenAuthenticationProvider sessionTokenProvider;
    private final SessionTokenAuthenticaitonProvider sessionProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var loginProcessingFilter = new RequestLoginProcessingFilter(successHandler, failureHandler,
                authenticationManagerBean());
        var sessionProcessingFilter = new SessionLoginProcessingFilter(failureHandler, authenticationManagerBean());
        http
                .authorizeRequests(authorize -> authorize.antMatchers("/api-docs", "/login", "/host", "/menu",
                        "/strings").permitAll())
                .authorizeRequests(authorize -> authorize.anyRequest().authenticated())
                .logout(logout -> logout.logoutUrl("/logout").permitAll().logoutSuccessHandler(logoutHandler))
                .exceptionHandling(handle -> handle.authenticationEntryPoint(failureEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable());
        http
                .addFilterBefore(loginProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(sessionProcessingFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(sessionProvider)
                .authenticationProvider(loginTokenProvider)
                .authenticationProvider(sessionTokenProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/**/*.js", "/**/*.css");
    }
}
