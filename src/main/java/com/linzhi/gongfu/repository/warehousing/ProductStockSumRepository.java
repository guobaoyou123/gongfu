package com.linzhi.gongfu.repository.warehousing;

import com.linzhi.gongfu.entity.ProductStockSum;
import com.linzhi.gongfu.entity.ProductStockSumId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductStockSumRepository extends CrudRepository<ProductStockSum, ProductStockSumId>, QuerydslPredicateExecutor<ProductStockSum> {


}
