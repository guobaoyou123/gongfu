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
    /**
     * 计划列表
     *
     * @param dcCompId  单位编码
     * @param createdBy 操作员编码
     * @return 计划列表
     */
    List<TemporaryPlan> findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedByOrderByCreatedAt(String dcCompId, String createdBy);

    /**
     * 更新需求数量
     *
     * @param demand          需求数量
     * @param temporaryPlanId 计划主键
     */
    @Modifying
    @Query("update TemporaryPlan as c set c.demand = ?1 where c.temporaryPlanId = ?2")
    @Transactional
    void updateNameById(BigDecimal demand, TemporaryPlanId temporaryPlanId);

    /**
     * 计划列表
     *
     * @param dcCompId  单位编码
     * @param createdBy 操作员编码
     * @param productId 产品主键
     * @return 计划列表
     */
    List<TemporaryPlan> findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedByAndTemporaryPlanId_ProductIdInOrderByCreatedAt(String dcCompId, String createdBy, List<String> productId);
}
