package com.linzhi.gongfu.security;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.linzhi.gongfu.entity.Company;
import com.linzhi.gongfu.entity.OperatorId;
import com.linzhi.gongfu.entity.Session;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.repository.CompanyRepository;
import com.linzhi.gongfu.repository.OperatorRepository;
import com.linzhi.gongfu.security.exception.NonexistentTokenException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;

/**
 * 在Spring Security中用于管理用户令牌的功能类
 *
 * @author xutao
 * @create_at 2021-12-22
 */
@Component
@RequiredArgsConstructor
public final class TokenStore {
    private final CompanyRepository companyRepository;
    private final OperatorRepository operatorRepository;
    private final RedisTemplate<String, Session> sessionTemplate;

    /**
     * 将给定的令牌组装成Redis存储键
     *
     * @param domain 用户所属公司所使用的二级域名
     * @param token  用户令牌
     * @return 用户令牌对应的Redis存储键
     * @throws IllegalArgumentException 如果提供了空的域名或者令牌，将会抛出异常
     */
    private String assembleKey(String domain, String token) throws IllegalArgumentException {
        Assert.notNull(domain, "必须提供操作员所属的企业域名。");
        Assert.notNull(token, "提供的操作员令牌为空白。");
        return String.join(":", "SESSION", domain.toUpperCase(), "TOKEN", token);
    }

    /**
     * 在Redis中检查指定的令牌对应的键是否存在
     *
     * @param domain 用户所属公司所使用的二级域名
     * @param token  指定的用户令牌
     * @return 用户令牌对应的键是否存在
     * @throws IllegalArgumentException 如果提供了空的域名或者令牌，将会抛出异常
     */
    public boolean exists(String domain, String token) {
        return Boolean.TRUE.equals(sessionTemplate.hasKey(assembleKey(domain, token)));
    }

    /**
     * 作废指定的用户令牌
     *
     * @param domain 用户所属公司所使用的二级域名
     * @param token  用户令牌
     * @throws IllegalArgumentException 如果提供了空的域名或者令牌，将会抛出异常
     */
    public void revoke(String domain, String token) {
        sessionTemplate.delete(assembleKey(domain, token));
    }

    /**
     * 生成一个基于UUID算法的用户令牌
     *
     * @return 代表用户令牌的UUID字符串
     */
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成指定用户的会话信息，并将其保存到Redis中
     *
     * @param operatorCode 用户的操作员编号
     * @param domain       用户所属公司所使用的二级域名
     * @param token        用户令牌
     * @param expiresAfter 用户令牌的过期时间，或用户有效登录时间
     * @param unit         描述用户令牌过期时间的时间单位
     * @throws IllegalArgumentException 如果提供了空的域名或者令牌，将会抛出异常
     */
    public void store(String operatorCode, String domain, String token, Long expiresAfter, TimeUnit unit) {
        Assert.notNull(domain, "必须提供操作员所属的企业域名。");
        Assert.notNull(token, "提供的操作员令牌为空白。");
        var operation = sessionTemplate.opsForValue();
        companyRepository.findBySubdomainName(domain)
                .map(Company::getCode)
                .flatMap(code -> operatorRepository
                        .findById(OperatorId.builder().companyCode(code).operatorCode(operatorCode).build()))
                .ifPresent(operator -> {
                    var session = Session.builder()
                            .token(token)
                            .operatorCode(operator.getIdentity().getOperatorCode())
                            .operatorName(operator.getName())
                            .companyCode(operator.getCompany().getCode())
                            .companyName(operator.getCompany().getNameInChinese())
                            .admin(operator.getAdmin().equals(Whether.YES))
                            .expriesAt(LocalDateTime.now().plus(expiresAfter, unit.toChronoUnit()))
                            .build();
                    operation.set(
                            assembleKey(domain, token),
                            session,
                            expiresAfter,
                            unit);
                });
    }

    /**
     * 从Redis中获取用户会话信息
     *
     * @param domain 用户所属公司所使用的二级域名
     * @param token  用户令牌
     * @return 用户目前的会话信息
     * @throws IllegalArgumentException  如果提供了空的域名或者令牌，将会抛出异常
     * @throws NonexistentTokenException 表示Redis中目前没有保存用户令牌或其对应的用户会话信息
     */
    public Session fetch(String domain, String token) throws IllegalArgumentException, NonexistentTokenException {
        var operation = sessionTemplate.opsForValue();
        var tokenKey = assembleKey(domain, token);
        return Optional.of(tokenKey)
                .map(operation::get)
                .orElseThrow(NonexistentTokenException::new);
    }

    /**
     * 延长目前用户的会话信息的有效期<br/>
     * 注意：用户会话的有效期是从上一个用户会话的过期时间开始延长，不是调用本方法的当前时间。
     *
     * @param domain       用户所属公司所使用的二级域名
     * @param token        用户令牌
     * @param expiresAfter 用户会话延长时间
     * @param unit         描述用户会话延长时间的单位
     * @throws IllegalArgumentException  如果提供了空的域名或者令牌，将会抛出异常
     * @throws NonexistentTokenException 表示Redis中目前没有保存用户令牌活期对应的用户会话信息
     */
    public void renew(String domain, String token, Long expiresAfter, TimeUnit unit) throws NonexistentTokenException {
        var operation = sessionTemplate.opsForValue();
        var tokenKey = assembleKey(domain, token);
        var renewedSession = Optional.of(tokenKey)
                .map(operation::get)
                .map(session -> session.withExpriesAt(session.getExpriesAt().plus(expiresAfter, unit.toChronoUnit())))
                .orElseThrow(NonexistentTokenException::new);
        operation.set(
                tokenKey,
                renewedSession,
                Duration.between(LocalDateTime.now(), renewedSession.getExpriesAt()));
    }
}
