package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Brand;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 操作品牌的Repository
 *
 * @author zgh
 * @create_at 2022-01-21
 */
public interface BrandRepository extends CrudRepository<Brand, String>, QuerydslPredicateExecutor<Brand> {

}
