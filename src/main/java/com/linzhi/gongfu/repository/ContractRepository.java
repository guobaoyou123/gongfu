package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.ContractList;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContractRepository
    extends CrudRepository<ContractList, String>, QuerydslPredicateExecutor<ContractList> {
    @Query(value="select  count(distinct c.id) " +
        "from contract_base c ,contract_rev  r  " +
        "where  c.created_by_comp=?1 and c.id = r.id and r.order_code=?2  " +
        "and c.type = '0' ",
        nativeQuery = true)
    int findByOrderCode(String dcCompId, String orderCode);

    @Query(value="select  count(distinct c.id) from contract_base c ,contract_rev  r " +
        " where  c.created_by_comp=?1 and c.id = r.id and r.order_code=?2  and c.type = '0'" +
        " and id <> ?3",
        nativeQuery = true)
    int findByOrderCode(String dcCompId, String orderCode,String contractId);
    @Query(value = "select  c.id from contract_base c ,contract_rev  r  where  c.created_by_comp =?1   and c.id = r.id  and c.type = '0' and r.fingerprint =?2 and r.revision=(select max(revision) from contract_rev v where v.id = r.id) "  ,nativeQuery = true)
    List<String> findContractId(String dcCompId, String sequenceCode);

    @Query(value = "select b.*,o.name as createdByName ,c.code as salesContractCode,r.order_code as salesOrderCode,d.order_code as order_code ,d.revision as revision,d.saler_order_code as supplierContractNo ," +
        "case b.state when '0' then count(distinct t.product_id)\n" +
        "else  count(distinct v.product_id)\n" +
        "end as category,\n" +
        "case b.state when '1' then d.confirm_total_price_vat\n" +
        "else d.total_price_vat end as taxedTotal\n"+
        " from   contract_base b\n" +
        "left join comp_operator o on b.created_by_comp = o.dc_comp_id and b.created_by = o.code\n" +
        "left join contract_base c on c.id = b.sales_contract_id\n" +
        "left join contract_rev r on r.id = b.sales_contract_id  and r.revision in (select max(revision) from contract_rev  where id = r.id)\n" +
        "left join contract_rev d on d.id = b.id  and d.revision in (select max(revision) from contract_rev  where id = d.id)\n" +
        "left join contract_record_temp t on t.contract_id = d.id\n" +
        "left join contract_record_rev v on v.contract_id = d.id and v.revision = d.revision\n" +
        " where  b.created_by_comp=?1 and b.created_by=?2 and b.type=?3 and b.state=?4 \n" +
        "group by b.id,o.name \n" +
        "      ,b.code\n" +
        "      ,b.sales_contract_id\n" +
        "      ,b.type\n" +
        "      ,b.created_by_comp\n" +
        "      ,b.created_by\n" +
        "      ,b.buyer_comp\n" +
        "      ,b.buyer_comp_name\n" +
        "      ,b.saler_comp\n" +
        "      ,b.saler_comp_name\n" +
        "      ,b.created_at\n" +
        "      ,b.state ,c.code ,r.order_code ,d.order_code,d.saler_order_code ,d.revision,  \n" +
        "   d.confirm_total_price_vat,d.total_price_vat\n" +
        "order by b.created_at desc,cast(RIGHT(b.code,3) as int )  desc ",
        nativeQuery = true)
    List<ContractList> findContractList(String compId, String operator, String  type, String  state);

    @Query(value = "select b.*,o.name as createdByName ,c.code as salesContractCode,r.order_code as salesOrderCode,d.order_code as order_code,d.revision as revision ,d.saler_order_code as supplierContractNo ," +
        "case b.state when '0' then count(distinct t.product_id)\n" +
        "else  count(distinct v.product_id)\n" +
        "end as category,\n" +
        "case b.state when '1' then d.confirm_total_price_vat\n" +
        "else d.total_price_vat end as taxedTotal\n"+
        " from   contract_base b\n" +
        "left join comp_operator o on b.created_by_comp = o.dc_comp_id and b.created_by = o.code\n" +
        "left join contract_base c on c.id = b.sales_contract_id\n" +
        "left join contract_rev r on r.id = b.sales_contract_id  and r.revision in (select max(revision) from contract_rev  where id = r.id)\n" +
        "left join contract_rev d on d.id = b.id  and d.revision in (select max(revision) from contract_rev  where id = d.id)\n" +
        "left join contract_record_temp t on t.contract_id = d.id\n" +
        "left join contract_record_rev v on v.contract_id = d.id and v.revision = d.revision\n" +
        " where  b.created_by_comp=?1  and b.type=?2 and b.state=?3 \n" +
        "group by b.id,o.name \n" +
        "      ,b.code\n" +
        "      ,b.sales_contract_id\n" +
        "      ,b.type\n" +
        "      ,b.created_by_comp\n" +
        "      ,b.created_by\n" +
        "      ,b.buyer_comp\n" +
        "      ,b.buyer_comp_name\n" +
        "      ,b.saler_comp\n" +
        "      ,b.saler_comp_name\n" +
        "      ,b.created_at\n" +
        "      ,b.state ,c.code ,r.order_code ,d.order_code ,d.saler_order_code,d.revision,  \n" +
        "   d.confirm_total_price_vat,d.total_price_vat\n" +
        "order by b.created_at desc,cast(RIGHT(b.code,3) as int )  desc ",
        nativeQuery = true)
    List<ContractList> findContractList(String compId, String  type, String  state);


}
