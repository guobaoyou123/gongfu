package com.linzhi.gongfu.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;

/**
 * 用于保存操作员非登录请求时提供的令牌进行验证的登录信息
 *
 * @author xutao
 * @create_at 2021-12-29
 */
public class OperatorAuthenticationToken extends AbstractAuthenticationToken {
    private final String domain;
    private final String token;

    public OperatorAuthenticationToken(String domain, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.domain = domain;
        this.token = token;
        super.setAuthenticated(true);
    }

    public OperatorAuthenticationToken(String domain, String token) {
        this(domain, token, Collections.emptyList());
    }

    /**
     * 这个登录信息是使用的Token进行验证，不使用密码等信息
     *
     * @return 空
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * 返回操作员提供的公司二级域名称以及登录令牌信息
     *
     * @return 以字符串数组保存的公司二级域名称和登录令牌
     */
    @Override
    public Object getPrincipal() {
        return new String[]{this.domain, this.token};
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        Assert.isTrue(authenticated, "本Token仅用于存放操作员登录请求，其中内容不可被信任！");
        super.setAuthenticated(false);
    }
}
