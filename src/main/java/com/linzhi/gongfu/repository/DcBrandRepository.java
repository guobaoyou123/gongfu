package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.DcBrand;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 系统品牌的Repository
 *
 * @author zgh
 * @create_at 2022-01-21
 */
public interface DcBrandRepository extends CrudRepository<DcBrand, String>, QuerydslPredicateExecutor<DcBrand> {

}
