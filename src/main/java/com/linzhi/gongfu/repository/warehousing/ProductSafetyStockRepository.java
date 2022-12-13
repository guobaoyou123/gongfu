package com.linzhi.gongfu.repository.warehousing;

import com.linzhi.gongfu.entity.ProductSafetyStock;
import com.linzhi.gongfu.entity.ProductSafetyStockId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ProductSafetyStockRepository  extends CrudRepository<ProductSafetyStock, ProductSafetyStockId>, QuerydslPredicateExecutor<ProductSafetyStock> {


}
