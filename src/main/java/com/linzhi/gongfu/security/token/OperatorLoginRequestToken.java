package com.linzhi.gongfu.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;

/**
 * 用于存放操作员登录表单信息的令牌
 *
 * @author xutao
 * @create_at 2021-12-29
 */
public class OperatorLoginRequestToken extends AbstractAuthenticationToken {
    private final String code;
    private final String domain;
    private String credentials;

    /**
     * 构建存放操作员登录信息的Token
     * <p>
     * 注意，这个用于保存登录请求信息的Token会自动被设置为可信任的，但不要将其应用在日常保存用户登录信息中。
     * </p>
     *
     * @param code        操作员编号
     * @param domain      操作员所属公司的二级域名称
     * @param password    操作员的密码，使用明文存储
     * @param authorities 用户请求的权限
     */
    public OperatorLoginRequestToken(String code, String domain, String password, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.code = code;
        this.domain = domain;
        this.credentials = password;
        super.setAuthenticated(true);
    }

    /**
     * 构建存放操作员登录信息的Token，构建仅包含空白权限列表的Token
     * <p>
     * 注意，这个用于保存登录请求信息的Token会自动被设置为可信任的，但不要将其应用在日常保存用户登录信息中。
     * </p>
     *
     * @param code     操作员编号
     * @param domain   操作员所属公司的二级域名称
     * @param password 操作员的密码，使用明文存储
     */
    public OperatorLoginRequestToken(String code, String domain, String password) {
        this(code, domain, password, Collections.emptyList());
    }

    /**
     * 获取操作员请求登录时提供的密码
     *
     * @return 操作员登录时提供的密码明文
     */
    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    /**
     * 获取用户用于认证的信息
     *
     * @return 使用字符串数组存放的操作员编号和操作员所述公司的二级域名称
     */
    @Override
    public Object getPrincipal() {
        return new String[]{code, domain};
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        Assert.isTrue(authenticated, "本Token仅用于存放操作员登录请求，其中内容不可被信任！");
        super.setAuthenticated(false);
    }
}
