package com.linzhi.gongfu.security;

import com.linzhi.gongfu.entity.DSession;
import com.linzhi.gongfu.repository.OperatorRepository;
import com.linzhi.gongfu.security.exception.NonexistentTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 在Spring Security中用于管理用户令牌的功能类
 * @author xutao
 * @create_at 2021-12-22
 */
@Slf4j
@Component
@RequiredArgsConstructor
public final class TokenStore {
    private final OperatorRepository operatorRepository;
    private final RedisTemplate<String, DSession> sessionTemplate;

    /**
     * 将给定的令牌组装成Redis存储键
     * @param token 用户令牌
     * @return 用户令牌对应的Redis存储键
     */
    private String assembleKey(String token) {
        return String.join(":", "SESSION", "TOKEN", token);
    }

    /**
     * 在Redis中检查指定的令牌对应的键是否存在
     * @param token 指定的用户令牌
     * @return 用户令牌对应的键是否存在
     */
    private boolean exists(String token) {
        return Boolean.TRUE.equals(sessionTemplate.hasKey(assembleKey(token)));
    }

    /**
     * 作废指定的用户令牌
     * @param token 用户令牌
     */
    public void revoke(String token) {
        sessionTemplate.delete(assembleKey(token));
    }

    /**
     * 生成一个基于UUID算法的用户令牌
     * @return 代表用户令牌的UUID字符串
     */
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成指定用户的会话信息，并将其保存到Redis中
     * @param operatorCode 用户的操作员编号
     * @param token 用户令牌
     * @param expiresAfter 用户令牌的过期时间，或用户有效登录时间
     * @param unit 描述用户令牌过期时间的时间单位
     */
    public void store(String operatorCode, String token, Long expiresAfter, TimeUnit unit) {
        var operation = sessionTemplate.opsForValue();
        operatorRepository.findById(operatorCode)
                .ifPresent(operator -> {
                    var session = DSession.builder()
                            .token(token)
                            .operatorCode(operator.getCode())
                            .operatorName(operator.getName())
                            .companyCode(operator.getCompany().getCode())
                            .companyName(operator.getCompany().getNameInChinese())
                            .expriesAt(LocalDateTime.now().plus(expiresAfter, unit.toChronoUnit()))
                            .build();
                    operation.set(
                            assembleKey(token),
                            session,
                            expiresAfter,
                            unit
                    );
                });
    }

    /**
     * 从Redis中获取用户会话信息
     * @param token 用户令牌
     * @return 用户目前的会话信息
     * @throws NonexistentTokenException 表示Redis中目前没有保存用户令牌或其对应的用户会话信息
     */
    public DSession fetch(String token) throws NonexistentTokenException {
        var operation = sessionTemplate.opsForValue();
        return Optional.ofNullable(token)
                .map(this::assembleKey)
                .map(operation::get)
                .orElseThrow(NonexistentTokenException::new);
    }

    /**
     * 延长目前用户的会话信息的有效期<br/>
     * 注意：用户会话的有效期是从上一个用户会话的过期时间开始延长，不是调用本方法的当前时间。
     * @param token 用户令牌
     * @param expiresAfter 用户会话延长时间
     * @param unit 描述用户会话延长时间的单位
     * @throws NonexistentTokenException 表示Redis中目前没有保存用户令牌活期对应的用户会话信息
     */
    public void renew(String token, Long expiresAfter, TimeUnit unit) throws NonexistentTokenException {
        var operation = sessionTemplate.opsForValue();
        var renewedSession = Optional.ofNullable(token)
                .map(this::assembleKey)
                .map(operation::get)
                .map(session -> session.withExpriesAt(session.getExpriesAt().plus(expiresAfter, unit.toChronoUnit())))
                .orElseThrow(NonexistentTokenException::new);
        operation.set(
                assembleKey(token),
                renewedSession,
                Duration.between(LocalDateTime.now(), renewedSession.getExpriesAt())
        );
    }
}
