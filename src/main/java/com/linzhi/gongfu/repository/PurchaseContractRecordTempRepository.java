package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PurchaseContractRecordTemp;
import com.linzhi.gongfu.entity.PurchaseContractRecordTempId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
/**
 * 采购合同临时明细的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface PurchaseContractRecordTempRepository
    extends CrudRepository<PurchaseContractRecordTemp, PurchaseContractRecordTempId>, QuerydslPredicateExecutor<PurchaseContractRecordTemp> {
    /**
     * 合同明细临时记录列表
     *
     * @param id 合同主键
     * @return 合同明细临时记录列表
     */
    @Cacheable(value = "contract_revision_recordTemp_detail;1800", key = "#id")
    List<PurchaseContractRecordTemp> findContractRecordTempsByPurchaseContractRecordTempId_ContractId(String id);

    /**
     * 最大编码
     *
     * @param id 合同主键
     * @return 编码
     */
    @Query(value = "select  max(code)  from purchase_contract_record_temp  where  contract_id=?1 ",
        nativeQuery = true)
    String findMaxCode(String id);

    /**
     * 删除合同明细临时记录
     *
     * @param id 合同主键
     */
    @Modifying
    @Query("delete from PurchaseContractRecordTemp as c  where c.purchaseContractRecordTempId.contractId=?1")
    void deleteProducts(String id);

    /**
     * 查找孪生合同明细临时记录列表
     *
     * @param  id 合同主键
     * @return 合同明细临时记录列表
     */
    @Query(value = "select *  from \n" +
        "(select  product_id,count(quantity) as quantity ,max(charge_unit) as charge_unit,max(vat_rate) as vat_rate   from purchase_contract_record_temp  where  contract_id=?1   group by product_id ) \n" +
        "as d \n" +
        "  order by product_id ",
        nativeQuery = true)
    List<PurchaseContractRecordTemp> findContractRecordTempsTwins(String id);
}
