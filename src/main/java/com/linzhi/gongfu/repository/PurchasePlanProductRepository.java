package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PurchasePlanProduct;
import com.linzhi.gongfu.entity.PurchasePlanProductId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;

public interface PurchasePlanProductRepository extends CrudRepository<PurchasePlanProduct, PurchasePlanProductId>, QuerydslPredicateExecutor<PurchasePlanProduct> {
    @Modifying
    @Query("update PurchasePlanProduct as c set c.demand =?1 where c.purchasePlanProductId=?2")
    int updateDemandById(BigDecimal demand, PurchasePlanProductId purchasePlanProductId);
    @Modifying
    @Query("delete from PurchasePlanProduct as c  where c.purchasePlanProductId.dcCompId=?1 and  c.purchasePlanProductId.planCode=?2 ")
    void  deleteProduct(String dcCompId , String planCode);
}
