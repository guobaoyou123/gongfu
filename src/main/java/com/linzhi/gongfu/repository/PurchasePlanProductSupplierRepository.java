package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PurchasePlanProductSupplier;
import com.linzhi.gongfu.entity.PurchasePlanProductSupplierId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;

public interface PurchasePlanProductSupplierRepository extends CrudRepository<PurchasePlanProductSupplier, PurchasePlanProductSupplierId> , QuerydslPredicateExecutor<PurchasePlanProductSupplier> {

    @Modifying
    @Query("update PurchasePlanProductSupplier as c set c.demand =?1 where c.purchasePlanProductSalerId=?2")
    int updateDemandById(BigDecimal demand, PurchasePlanProductSupplierId purchasePlanProductSupplierId);


}
