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

    @Cacheable(value = "contract_revision_recordTemp_detail;1800",key = "#id")
    List<ContractRecordTemp> findContractRecordTempsByContractRecordTempId_ContractId(String id);
    @Query(value="select  max(code)  from contract_record_temp  where  contract_id=?1 ",
        nativeQuery = true)
    String findMaxCode(String id);


    @Modifying
    @Query("delete from ContractRecordTemp as c  where c.contractRecordTempId.contractId=?1")
    void  deleteProducts(String id);
}
