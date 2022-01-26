package com.linzhi.gongfu.repository;

import java.util.Optional;

import com.linzhi.gongfu.entity.EnrolledCompany;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 入格公司信息Repository
 *
 * @author xutao
 * @create_at 2022-01-14
 */
public interface EnrolledCompanyRepository
        extends CrudRepository<EnrolledCompany, String>, QuerydslPredicateExecutor<EnrolledCompany> {
    Optional<EnrolledCompany> findBySubdomainName(String subdomainName);
}
