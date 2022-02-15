package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PurchasePlan;
import com.linzhi.gongfu.entity.PurchasePlanId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PurchasePlanRepository extends CrudRepository<PurchasePlan, PurchasePlanId> , QuerydslPredicateExecutor<PurchasePlan> {
     Optional<PurchasePlan> findFirstByCreatedByAndPurchasePlanId_DcCompIdOrderByCreatedAtDesc(String createdBy, String dcCompId);
    @Query(value="select  right('000'+cast(cast(max(right(plan_code,2)) as int)+1 as varchar(2)),2) from purchase_plan  where  dc_comp_id=?1 and created_by=?2\n" +
        "             and DateDiff(dd,created_at,GETDATE())=0",
        nativeQuery = true)
    String findMaxCode(String dcCompId, String createdBy, LocalDate createdAt);
}
