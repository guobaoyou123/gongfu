package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Product;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product,String>, QuerydslPredicateExecutor<Product> {

}
