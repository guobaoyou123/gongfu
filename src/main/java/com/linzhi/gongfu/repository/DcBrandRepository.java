package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.DcBrand;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface DcBrandRepository extends CrudRepository<DcBrand, String>, QuerydslPredicateExecutor<DcBrand> {

}
