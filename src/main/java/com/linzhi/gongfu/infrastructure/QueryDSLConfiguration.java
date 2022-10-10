package com.linzhi.gongfu.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 配置QueryDSL所要使用的JPAQueryFactory。
 *
 * @author xutao
 * @create_at 2022-01-26
 */
@RequiredArgsConstructor
@Configuration
public class QueryDSLConfiguration {
    @PersistenceContext
    private final EntityManager entityManager;

    @Bean
    @Primary
    public JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(this.entityManager);
    }
}
