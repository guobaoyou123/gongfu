package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PurchasePlanProduct;
import com.linzhi.gongfu.entity.PurchasePlanProductId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface PurchasePlanProductRepository extends CrudRepository<PurchasePlanProduct, PurchasePlanProductId>, QuerydslPredicateExecutor<PurchasePlanProduct> {
}
