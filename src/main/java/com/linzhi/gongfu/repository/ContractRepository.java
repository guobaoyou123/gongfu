package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Contract;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ContractRepository
    extends CrudRepository<Contract, String>, QuerydslPredicateExecutor<Contract> {
    @Query(value="select  count(*) from contract_base  where  created_by_comp=?1 and order_code=?2  and type = '0'",
        nativeQuery = true)
    int findByOrderCode(String dcCompId, String orderCode);
    @Query(value = "select id  from  contract_base where  created_by_comp =?1    and type = '0' and sequence_code =?2 "  ,nativeQuery = true)
    List<String>  findContractId(String dcCompId, String sequenceCode);

}
