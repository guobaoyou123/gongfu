package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Contract;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ContractRepository
    extends CrudRepository<Contract, String>, QuerydslPredicateExecutor<Contract> {
    @Query(value="select  count(c.id) from contract_base c ,contract_rev  r  where  created_by_comp=?1 and order_code=?2  and type = '0'",
        nativeQuery = true)
    int findByOrderCode(String dcCompId, String orderCode);
    @Query(value = "select  c.id from contract_base c ,contract_rev  r  where  created_by_comp =?1    and type = '0' and fingerprint =?2 "  ,nativeQuery = true)
    List<String> findContractId(String dcCompId, String sequenceCode);
}
