package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.ContractRecord;
import com.linzhi.gongfu.entity.ContractRecordId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContractRecordRepository
    extends CrudRepository<ContractRecord, ContractRecordId>, QuerydslPredicateExecutor<ContractRecord> {

    /**
     * 合同明细列表
     *
     * @param id       合同主键
     * @param revision 版本号
     * @return 合同明细列表
     */
    @Cacheable(value = "contract_revision_record_detail;1800", key = "#id+'-'+#revision")
    List<ContractRecord> findContractRecordsByContractRecordId_ContractIdAndContractRecordId_Revision(String id, int revision);
}
