package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompBrandAuth;
import com.linzhi.gongfu.entity.CompBrandAuthId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompBrandAuthRepository extends CrudRepository<CompBrandAuth, CompBrandAuthId>, QuerydslPredicateExecutor<CompBrandAuth> {
    List<CompBrandAuth> findCompBrandAuthByCompBrandAuthId_BeAuthComp(String beAuthComp);
}
