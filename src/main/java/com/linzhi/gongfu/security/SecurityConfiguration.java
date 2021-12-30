package com.linzhi.gongfu.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var loginProcessingFilter = new RequestLoginProcessingFilter(successHandler, failureHandler, authenticationManagerBean());
        var sessionProcessingFilter = new SessionLoginProcessingFilter(failureHandler, authenticationManagerBean());
        http
            .addFilterBefore(loginProcessingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(sessionProcessingFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .logout()
            .logoutUrl("/logout").permitAll()
            .logoutSuccessHandler(logoutHandler)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(failureEntryPoint)
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .csrf().disable()
            .cors().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth
            .authenticationProvider(loginTokenProvider)
            .authenticationProvider(sessionTokenProvider);
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
