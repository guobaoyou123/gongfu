package com.linzhi.gongfu.security;

import com.linzhi.gongfu.security.token.OperatorSessionToken;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 用于对生成的操作员会话Token进行处理
 *
 * <p>
 * 因为Spring Security必须对每一种类型的Token进行处理，所以对于保存有用户会话信息的Token也必须进行处理。
 * 在这个处理器中，只需要设置Tokne的认证状态为{@code true}即可，不需要其他的设置。
 * </p>
 *
 * @author xutao
 * @create_at 2021-12-30
 */
@Component
@NoArgsConstructor
public class SessionTokenAuthenticaitonProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        authentication.setAuthenticated(true);
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (OperatorSessionToken.class.isAssignableFrom(authentication));
    }
}
