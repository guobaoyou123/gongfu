package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.ContractRecordTemp;
import com.linzhi.gongfu.entity.ContractRecordTempId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContractRecordTempRepository
    extends CrudRepository<ContractRecordTemp, ContractRecordTempId>, QuerydslPredicateExecutor<ContractRecordTemp> {
    /**
     * 合同明细临时记录列表
     *
     * @param id 合同主键
     * @return 合同明细临时记录列表
     */
    @Cacheable(value = "contract_revision_recordTemp_detail;1800", key = "#id")
    List<ContractRecordTemp> findContractRecordTempsByContractRecordTempId_ContractId(String id);

    /**
     * 最大编码
     *
     * @param id 合同主键
     * @return 编码
     */
    @Query(value = "select  max(code)  from contract_record_temp  where  contract_id=?1 ",
        nativeQuery = true)
    String findMaxCode(String id);

    /**
     * 删除合同明细临时记录
     *
     * @param id 合同主键
     */
    @Modifying
    @Query("delete from ContractRecordTemp as c  where c.contractRecordTempId.contractId=?1")
    void deleteProducts(String id);
}
