package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Brand;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface BrandRepository extends CrudRepository<Brand, String>, QuerydslPredicateExecutor<Brand> {

}
