package com.linzhi.gongfu.repository.trade;

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


}
