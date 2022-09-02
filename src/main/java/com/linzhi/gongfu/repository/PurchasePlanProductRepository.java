package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PurchasePlanProduct;
import com.linzhi.gongfu.entity.PurchasePlanProductId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;

public interface PurchasePlanProductRepository extends CrudRepository<PurchasePlanProduct, PurchasePlanProductId>, QuerydslPredicateExecutor<PurchasePlanProduct> {
    /**
     * 更新采购计划需求
     *
     * @param demand                需求数量
     * @param purchasePlanProductId 采购计划主键
     * @return 是否成功
     */
    @Modifying
    @Query("update PurchasePlanProduct as c set c.demand =?1 where c.purchasePlanProductId=?2")
    int updateDemandById(BigDecimal demand, PurchasePlanProductId purchasePlanProductId);

    /**
     * 删除采购 计划产品
     *
     * @param dcCompId 单位编码
     * @param planCode 采购计划编码
     */
    @Modifying
    @Query("delete from PurchasePlanProduct as c  where c.purchasePlanProductId.dcCompId=?1 and  c.purchasePlanProductId.planCode=?2 ")
    void removeProduct(String dcCompId, String planCode);

}
