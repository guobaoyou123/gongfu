package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PurchaseContractRecord;
import com.linzhi.gongfu.entity.PurchaseContractRecordId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
/**
 * 采购合同明细的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface PurchaseContractRecordRepository
    extends CrudRepository<PurchaseContractRecord, PurchaseContractRecordId>, QuerydslPredicateExecutor<PurchaseContractRecord> {

    /**
     * 合同明细列表
     *
     * @param id       合同主键
     * @param revision 版本号
     * @return 合同明细列表
     */
    @Cacheable(value = "contract_revision_record_detail;1800", key = "#id+'-'+#revision")
    List<PurchaseContractRecord> findContractRecordsByPurchaseContractRecordId_ContractIdAndPurchaseContractRecordId_Revision(String id, int revision);
}
