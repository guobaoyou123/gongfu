package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PurchasePlan;
import com.linzhi.gongfu.entity.PurchasePlanId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 采购计划基础信息的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface PurchasePlanRepository extends CrudRepository<PurchasePlan, PurchasePlanId>, QuerydslPredicateExecutor<PurchasePlan> {
    /**
     * 最大编码
     *
     * @param dcCompId  单位编码
     * @param createdBy 操作员编码
     * @param createdAt 创建时间
     * @return 编码
     */
    @Query(value = "select  right(('000'+cast((cast(max(right(plan_code,2)) as int)+1) as varchar)),2) from purchase_plan  where  dc_comp_id=?1 and created_by=?2\n" +
        "             and DateDiff(dd,created_at,GETDATE())=0",
        nativeQuery = true)
    String findMaxCode(String dcCompId, String createdBy, LocalDate createdAt);

    /**
     * 删除计划
     *
     * @param purchasePlanId 主键
     */
    @Modifying
    @Query("delete from PurchasePlan as c  where c.purchasePlanId=?1 ")
    void deletePurchasePlan(PurchasePlanId purchasePlanId);

    /**
     * 查找采购计划详情
     *
     * @param dcCompId  单位编码
     * @param createdBy 操作员编码
     * @return 采购计划详情
     */
    Optional<PurchasePlan> findFirstByPurchasePlanId_DcCompIdAndCreatedBy(String dcCompId, String createdBy);

}
