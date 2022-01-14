package com.linzhi.gongfu.repository;

import java.util.Optional;

import com.linzhi.gongfu.entity.EnrolledCompany;

import org.springframework.data.repository.CrudRepository;

public interface EnrolledCompanyRepository extends CrudRepository<EnrolledCompany, String> {
    Optional<EnrolledCompany> findBySubdomainName(String subdomainName);
}
