package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Product;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, String>, QuerydslPredicateExecutor<Product> {
    /**
     * 产品列表
     *
     * @param id 产品主键列表
     * @return 产品列表
     */
    List<Product> findProductByIdIn(List<String> id);

    /**
     * 产品列表
     *
     * @param code 产品编码
     * @return 产品列表
     */
    List<Product> findProductByCode(String code);

    /**
     * 产品详情
     *
     * @param code      产品编码
     * @param brandCode 品牌编码
     * @return 产品详情
     */
    Optional<Product> findProductByCodeAndBrandCode(String code, String brandCode);
}
