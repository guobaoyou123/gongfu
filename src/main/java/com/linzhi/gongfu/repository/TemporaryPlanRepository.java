package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.TemporaryPlan;
import com.linzhi.gongfu.entity.TemporaryPlanId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface TemporaryPlanRepository extends CrudRepository<TemporaryPlan, TemporaryPlanId>, QuerydslPredicateExecutor<TemporaryPlan> {
    List<TemporaryPlan> findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedBy(String dcCompId,String createdBy);
    @Modifying
    @Query("update TemporaryPlan as c set c.demand = ?1 where c.temporaryPlanId = ?2")
    @Transactional
    void updateNameById(BigDecimal name, TemporaryPlanId temporaryPlanId);

    List<TemporaryPlan> findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedByAndTemporaryPlanId_ProductIdIn(String dcCompId, String createdBy, List<String> productId);
}
