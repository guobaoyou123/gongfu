package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PurchasePlan;
import com.linzhi.gongfu.entity.PurchasePlanId;
import com.linzhi.gongfu.entity.TemporaryPlanId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PurchasePlanRepository extends CrudRepository<PurchasePlan, PurchasePlanId> , QuerydslPredicateExecutor<PurchasePlan> {
   @Query(value="select  right(('000'+cast((cast(max(right(plan_code,2)) as int)+1) as varchar)),2) from purchase_plan  where  dc_comp_id=?1 and created_by=?2\n" +
        "             and DateDiff(dd,created_at,GETDATE())=0",
        nativeQuery = true)
    String findMaxCode(String dcCompId, String createdBy, LocalDate createdAt);

    @Modifying
    @Query("update TemporaryPlan as c set c.demand = ?1 where c.temporaryPlanId = ?2")
    @Transactional
    void updateNameById(BigDecimal name, TemporaryPlanId temporaryPlanId);

    @Modifying
    @Query("delete from PurchasePlan as c  where c.purchasePlanId=?1 ")
    void  deletePurchasePlan(PurchasePlanId purchasePlanId);

    Optional<PurchasePlan> findPurchasePlanByPurchasePlanId_DcCompIdAndCreatedBy(String dcCompId, String createdBy);

}
