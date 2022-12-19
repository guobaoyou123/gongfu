package com.linzhi.gongfu.repository.storage;

import com.linzhi.gongfu.entity.ProductStockSum;
import com.linzhi.gongfu.entity.ProductStockSumId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ProductStockSumRepository extends CrudRepository<ProductStockSum, ProductStockSumId>, QuerydslPredicateExecutor<ProductStockSum> {


}
