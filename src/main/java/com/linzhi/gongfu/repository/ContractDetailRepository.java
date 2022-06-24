package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.ContractDetail;
import com.linzhi.gongfu.enumeration.ContractState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ContractDetailRepository
    extends CrudRepository<ContractDetail, String>, QuerydslPredicateExecutor<ContractDetail> {

    @Query(value="select  right(('000'+cast((cast(max(right(code,3)) as int)+1) as varchar)),3) from contract_base  where  created_by_comp=?1 and created_by=?2\n" +
        "             and DateDiff(dd,created_at,GETDATE())=0 ",
        nativeQuery = true)
    Optional<String> findMaxCode(String dcCompId, String createdBy);


    @Modifying
    @Query(value = "update ContractDetail c set c.state=?1 where c.id=?2")
    void updateContractState(ContractState state ,String id);
}
