package com.linzhi.gongfu.security;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.linzhi.gongfu.entity.EnrolledCompany;
import com.linzhi.gongfu.entity.Operator;
import com.linzhi.gongfu.entity.OperatorId;
import com.linzhi.gongfu.entity.Scene;
import com.linzhi.gongfu.entity.Session;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.exception.UnexistOperatorException;
import com.linzhi.gongfu.repository.EnrolledCompanyRepository;
import com.linzhi.gongfu.repository.OperatorRepository;
import com.linzhi.gongfu.security.exception.NonexistentTokenException;
import com.linzhi.gongfu.security.exception.OperatorNotFoundException;
import com.linzhi.gongfu.security.token.OperatorAuthenticationToken;
import com.linzhi.gongfu.security.token.OperatorSessionToken;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 对操作员登录后的正常会话请求提供的会话Token进行处理的认证处理器
 *
 * @author xutao
 * @create_at 2021-12-29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public final class SessionRequestTokenAuthenticationProvider implements AuthenticationProvider {
    private final EnrolledCompanyRepository enrolledCompanyRepository;
    private final OperatorRepository operatorRepository;
    private final TokenStore tokenStore;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            Assert.isInstanceOf(OperatorAuthenticationToken.class, authentication);
            String[] principals = (String[]) authentication.getPrincipal();
            var session = tokenStore.fetch(principals[0], principals[1]);
            var operator = enrolledCompanyRepository.findBySubdomainName(principals[0])
                    .map(EnrolledCompany::getId)
                    .map(companyCode -> OperatorId.builder().companyCode(companyCode)
                            .operatorCode(session.getOperatorCode())
                            .build())
                    .flatMap(operatorRepository::findById)
                    .orElseThrow(UnexistOperatorException::new);
            return createSuccessAuthentication(session, operator, authentication);
        } catch (NonexistentTokenException e) {
            log.error("操作员的会话Token没有找到，会话可能已过期或者被伪造。");
            throw new BadCredentialsException("操作员的会话已经失效。");
        } catch (UnexistOperatorException e) {
            log.error("操作员没有找到，可能存在操作员被删除或者伪造。");
            throw new OperatorNotFoundException();
        } catch (IllegalArgumentException e) {
            log.error("认证处理器中出现了不能识别的内容或者不符合处理条件，{}", e.getMessage());
            throw new AuthenticationServiceException("不支持的认证信息处理：" + e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OperatorAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 根据给定的操作员和认证信息等内容，生成代表认证成功且保存有操作员会话信息的Token
     *
     * @param session        从令牌缓存中获取的操作员会话信息
     * @param operator       操作员信息
     * @param authentication 原始认证信息
     * @return 代表认证成功的会话Token
     */
    private OperatorSessionToken createSuccessAuthentication(Session session, Operator operator,
            Authentication authentication) {
        var privileges = new ArrayList<GrantedAuthority>();
        privileges.add(new SimpleGrantedAuthority("ROLE_OPERATOR"));
        if (operator.getAdmin().equals(Whether.YES)) {
            privileges.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        var sceneCodes = session.getScenes().stream()
                .map(Scene::getCode)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        privileges.addAll(sceneCodes);
        var sessionToken = new OperatorSessionToken(
                operator.getIdentity().getOperatorCode(),
                operator.getName(),
                operator.getCompany().getId(),
                operator.getCompany().getNameInCN(),
                operator.getCompany().getSubdomainName(),
                session.getToken(),
                session.getExpriesAt(),
                privileges);
        sessionToken.setDetails(authentication);
        return sessionToken;
    }
}
