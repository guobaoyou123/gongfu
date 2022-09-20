package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.SalesContractRecord;
import com.linzhi.gongfu.entity.SalesContractRecordId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
/**
 * 销售合同明细的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface SalesContractRecordRepository
    extends CrudRepository<SalesContractRecord, SalesContractRecordId>, QuerydslPredicateExecutor<SalesContractRecord> {

    /**
     * 合同明细列表
     *
     * @param id       合同主键
     * @param revision 版本号
     * @return 合同明细列表
     */
    @Cacheable(value = "contract_revision_record_detail;1800", key = "#id+'-'+#revision")
    List<SalesContractRecord> findContractRecordsBySalesContractRecordId_ContractIdAndSalesContractRecordId_Revision(String id, int revision);
}
