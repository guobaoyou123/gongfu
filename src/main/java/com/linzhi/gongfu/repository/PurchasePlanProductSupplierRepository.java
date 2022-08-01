package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PurchasePlanProductSupplier;
import com.linzhi.gongfu.entity.PurchasePlanProductSupplierId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PurchasePlanProductSupplierRepository extends CrudRepository<PurchasePlanProductSupplier, PurchasePlanProductSupplierId> , QuerydslPredicateExecutor<PurchasePlanProductSupplier> {

    @Modifying
    @Query("update PurchasePlanProductSupplier as c set c.demand =?1 where c.purchasePlanProductSupplierId=?2")
    int updateDemandById(BigDecimal demand, PurchasePlanProductSupplierId purchasePlanProductSupplierId);

    @Modifying
    @Query("delete from PurchasePlanProductSupplier as c  where c.purchasePlanProductSupplierId.dcCompId=?1 and  c.purchasePlanProductSupplierId.planCode=?2 and  c.purchasePlanProductSupplierId.productId in ?3")
    void  removeSupplier(String dcCompId , String planCode, List<String> productId);

    @Modifying
    @Query("delete from PurchasePlanProductSupplier as c  where c.purchasePlanProductSupplierId.dcCompId=?1 and  c.purchasePlanProductSupplierId.planCode=?2 ")
    void  removeSupplier(String dcCompId , String planCode);

    @Query(value = "select distinct saler_code,saler_name from  purchase_plan_product_saler  where dc_comp_id=?1 and plan_code=?2 and demand>0 ",nativeQuery = true)
    List<Map<String,String>> listDistinctSuppliers(String dcCompId , String planCode);

    /**
     * 查询最大序号
     * @param productId
     * @param planCode
     * @param dcCompId
     * @return
     */
    @Query("select  max(p.serial)+1 from PurchasePlanProductSupplier p where p.purchasePlanProductSupplierId.productId=?1 and p.purchasePlanProductSupplierId.planCode=?2 and  p.purchasePlanProductSupplierId.dcCompId=?3")
    int  findMaxSerial(String productId,String planCode,String dcCompId);

}
