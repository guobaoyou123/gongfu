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
    @Query(value="select  count(c.id) from contract_base c ,contract_rev  r  where  c.created_by_comp=?1 and c.id = r.id and r.order_code=?2  and c.type = '0' and r.revision=(select max(revision) from contract_rev v where v.id = r.id) ",
        nativeQuery = true)
    int findByOrderCode(String dcCompId, String orderCode);
    @Query(value = "select  c.id from contract_base c ,contract_rev  r  where  c.created_by_comp =?1   and c.id = r.id  and c.type = '0' and r.fingerprint =?2 and r.revision=(select max(revision) from contract_rev v where v.id = r.id) "  ,nativeQuery = true)
    List<String> findContractId(String dcCompId, String sequenceCode);

    @Query(value = "select b.*,o.name as createdByName ,c.code as salesContractCode,r.order_code as salesOrderCode,d.order_code as order_code ,d.revision as revision,'23' as supplierContractNo  from   contract_base b\n" +
        "left join comp_operator o on b.created_by_comp = o.dc_comp_id and b.created_by = o.code\n" +
        "left join contract_base c on c.id = b.sales_contract_id\n" +
        "left join contract_rev r on r.id = b.sales_contract_id  and r.revision in (select max(revision) from contract_rev  where id = r.id)\n" +
        "left join contract_rev d on d.id = b.id  and d.revision in (select max(revision) from contract_rev  where id = d.id)\n" +

        " where  b.created_by_comp=?1 and b.created_by=?2 and b.type=?3 and b.state=?4 order by b.created_at desc,cast(RIGHT(b.code,3) as int )  desc ",
        nativeQuery = true)
    List<Contract> findContractList(String compId, String operator, String  type, String  state);

    @Query(value = "select b.*,o.name as createdByName ,c.code as salesContractCode,r.order_code as salesOrderCode,d.order_code as order_code,d.revision as revision ,'23' as supplierContractNo  from   contract_base b\n" +
        "left join comp_operator o on b.created_by_comp = o.dc_comp_id and b.created_by = o.code\n" +
        "left join contract_base c on c.id = b.sales_contract_id\n" +
        "left join contract_rev r on r.id = b.sales_contract_id  and r.revision in (select max(revision) from contract_rev  where id = r.id)\n" +
        "left join contract_rev d on d.id = b.id  and d.revision in (select max(revision) from contract_rev  where id = d.id)\n" +

        " where  b.created_by_comp=?1  and b.type=?2 and b.state=?3 order by b.created_at desc,cast(RIGHT(b.code,3) as int )  desc ",
        nativeQuery = true)
    List<Contract> findContractList(String compId, String  type, String  state);
}
