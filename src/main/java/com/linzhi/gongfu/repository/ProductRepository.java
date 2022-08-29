package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Product;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, String>, QuerydslPredicateExecutor<Product> {
    List<Product> findProductByIdIn(List<String> id);

    List<Product> findProductByCode(String code);

    Optional<Product> findProductByCodeAndBrandCode(String code, String brandCode);
}
