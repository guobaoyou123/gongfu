package com.linzhi.gongfu.security;

import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.dto.TOperatorInfo;
import com.linzhi.gongfu.dto.TScene;
import com.linzhi.gongfu.entity.OperatorId;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.security.exception.NoFoundPasswordException;
import com.linzhi.gongfu.security.token.OperatorLoginRequestToken;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.CompanyService;
import com.linzhi.gongfu.service.OperatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 对操作员的登录请求Token进行处理的认证处理器
 *
 * @author xutao
 * @create_at 2021-12-29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public final class LoginRequestTokenAuthenticationProvider implements AuthenticationProvider {
    private final CompanyService companyService;
    private final OperatorService operatorService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            Assert.isInstanceOf(OperatorLoginRequestToken.class, authentication, "传入了不支持的认证请求信息类型。");
            String[] principals = (String[]) authentication.getPrincipal();
            log.info("操作员 [{}@{}] 请求登录。", principals[0], principals[1]);
            if (Objects.isNull(authentication.getCredentials())) {
                log.info("操作员 [{}@{}] 试图使用空白的密码登录，登录请求已拒绝。", principals[0], principals[1]);
                throw new BadCredentialsException("操作员试图使用空白密码登录。");
            }
            var operator = companyService.findCompanyInformationByHostname(principals[1])
                .map(TCompanyBaseInformation::getCode)
                .map(companyCode -> OperatorId.builder().operatorCode(principals[0]).companyCode(companyCode)
                    .build())
                .flatMap(operatorService::findOperatorByID)
                .orElseThrow(() -> new UsernameNotFoundException("请求的操作员不存在"));
            if (operator.getChanged().equals(Whether.NO)) {
                log.info("操作员 [{}@{}] 需要重新设置密码。", principals[0], principals[1]);
                throw new NoFoundPasswordException();
            }
            // 这里对操作员的其他状态进行处理
            if (operator.getState().equals(Availability.DISABLED)) {
                log.info("操作员 [{}@{}] 试图登录，但因为已被禁用，登录请求已拒绝。", principals[0], principals[1]);
                throw new DisabledException("操作员已被停用。");
            }
            // 如果需要对密码进行加盐等额外处理，在此处进行
            var attemptPassword = authentication.getCredentials().toString();
            if (!passwordEncoder.matches(attemptPassword, operator.getPassword())) {
                log.info("操作员 [{}@{}] 试图使用错误的密码登录，登录请求已拒绝。", principals[0], principals[1]);
                throw new BadCredentialsException("操作员提供的密码不正确");
            }
            return createSuccessAuthentication(operator, authentication);
        } catch (IllegalArgumentException e) {
            log.error("认证处理器中出现了不能识别的内容或者不符合处理条件，{}", e.getMessage());
            throw new AuthenticationServiceException("不支持的认证信息处理：" + e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (OperatorLoginRequestToken.class.isAssignableFrom(authentication));
    }

    /**
     * 根据给定的操作员和认证信息等内容，生成代表认证成功且保存有操作员会话信息的Token
     *
     * @param operator       经过认证的操作员记录实体
     * @param authentication 原始登录表单信息
     * @return 代表认证成功的会话Token
     */
    private Authentication createSuccessAuthentication(TOperatorInfo operator, Authentication authentication) {
        var privileges = new ArrayList<GrantedAuthority>();
        privileges.add(new SimpleGrantedAuthority("ROLE_OPERATOR"));
        if (operator.getAdmin().equals(Whether.YES)) {
            privileges.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        var sceneCodes = operator.getScenes().stream()
            .map(TScene::getCode)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        privileges.addAll(sceneCodes);
        OperatorSessionToken sessionToken = new OperatorSessionToken(
            operator.getCode(),
            operator.getName(),
            operator.getCompanyCode(),
            operator.getCompanyName(),
            operator.getCompanyDomain(),
            null,
            operator.getAdmin(),
            null,
            privileges);
        sessionToken.setDetails(authentication);
        return sessionToken;
    }
}
