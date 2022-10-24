package com.linzhi.gongfu.security.token;

import com.linzhi.gongfu.enumeration.Whether;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

/**
 * 用于存放操作员登录会话认证信息的Token
 *
 * @author xutao
 * @create_at 2021-12-29
 */
public class OperatorSessionToken extends AbstractAuthenticationToken {
    @Getter
    private final Session session;

    /**
     * 使用操作员和操作员所属公司信息建立操作员会话信息
     *
     * @param operatorCode 操作员编号
     * @param operatorName 操作员名称
     * @param companyCode  操作员所属公司编号
     * @param companyName  操作员所属公司名称，名称不限定必须是中文或者其他语言
     * @param domain       操作员所属公司使用的二级域名称
     * @param token        操作员登录会话令牌
     * @param isAdmin        操作员是否为管理员
     * @param expiresAt    操作员登录会话过期时间
     * @param authories    操作员所拥有的权限列表
     */
    public OperatorSessionToken(
        String operatorCode,
        String operatorName,
        String companyCode,
        String companyName,
        String domain,
        String token,
        Whether isAdmin,
        LocalDateTime expiresAt,
        Collection<? extends GrantedAuthority> authories) {
        super(authories);
        this.session = Session.builder()
            .operatorCode(operatorCode)
            .operatorName(operatorName)
            .companyCode(companyCode)
            .companyName(companyName)
            .domain(domain)
            .isAdmin(isAdmin)
            .token(token)
            .expiresAt(expiresAt)
            .build();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return session;
    }

    @Override
    public boolean isAuthenticated() {
        var expired = Optional.ofNullable(this.session)
            .map(OperatorSessionToken.Session::getExpiresAt)
            .map(t -> t.isBefore(LocalDateTime.now()))
            .orElse(false);
        return !expired && super.isAuthenticated();
    }

    /**
     * 获取操作员名称
     *
     * @return 操作员名称
     */
    @Override
    public String getName() {
        return this.session.getOperatorName();
    }

    /**
     * 用于在SecurityContext中保存操作员登录会话的实体
     *
     * @author xutao
     * @create_at 2021-12-29
     */
    @Data
    @Builder
    @AllArgsConstructor
    public static final class Session {
        private final String operatorCode;
        private final String operatorName;
        private final String companyCode;
        private final String companyName;
        private final String domain;
        private String token;
        private final Whether isAdmin;
        private LocalDateTime expiresAt;
    }
}
