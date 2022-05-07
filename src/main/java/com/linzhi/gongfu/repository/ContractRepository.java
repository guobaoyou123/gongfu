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
    @Query(value = "select a.contract_id  from  contract_record a,contract_base b \n" +
        "where a.contract_id=b.id  and b.type = '0' and b.created_by_comp =?1  and b.created_by=?2 \n" +
        "group by contract_id \n" +
        "having count(a.*)=?3 and sum(quantity)=?4   ",nativeQuery = true)
    List<String>  findContractId(String dcCompId, String operator, int kinds , BigDecimal totalQuantity);

     List<Contract> findContractByIdIn(List<String> ids);
}
