package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.SalesContracts;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 销售合同列表的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface SalesContractsRepository
    extends CrudRepository<SalesContracts, String>, QuerydslPredicateExecutor<SalesContracts> {

    /**
     * 合同列表
     *
     * @param compId   单位编码
     * @param operator 操作员
     * @param state    状态
     * @return 合同列表
     */
    @Query(value = "select" +
        " b.id,b.code,d.revision,d.order_code as orderCode,o.code as createdBy,o.name as createdByName , b.created_at as createdAt,b.state," +
        "p.id as paired," +
        "case b.state when '0' then count(distinct  t.product_id) else count(distinct v.product_id) end as category " +
        ",d.buyer_order_code as customerContractNo ,b.buyer_comp as buyerCompCode  ,cb.chi_short  as buyerCompName," +
        "d.total_price_vat as taxedTotal,d.confirm_total_price_vat as confirmTaxedTotal from sales_contract_base  b " +
        "left join comp_operator o on o.code= b.created_by and o.dc_comp_id = b.created_by_comp " +
        "left join sales_contract_rev d on d.id = b.id and d.revision in (select max(dr.revision) from sales_contract_rev dr where dr.id = d.id) " +
        "left join sales_contract_record_rev v on v.contract_id=d.id and v.revision = d.revision " +
        "left join sales_contract_record_temp t on t.contract_id = d.id " +
        "left join comp_base cb on  b.buyer_comp = cb.code \n" +
        "left join purchase_contract_base p on b.paired_code=p.paired_code" +
        " where b.created_by_comp=?1 and b.created_by=?2 and b.state =?3   group by  b.id,b.code,d.revision,d.order_code,o.code,o.name, b.created_at,b.state,p.id,d.buyer_order_code,b.buyer_comp ,cb.chi_short,d.total_price_vat,d.confirm_total_price_vat \n", nativeQuery = true)
    List<SalesContracts> listContracts(String compId, String operator, String state);

    /**
     * 合同列表
     *
     * @param compId 单位编码
     * @param state  状态
     * @return 合同列表
     */
    @Query(value = "select " +
        " b.id,b.code,d.revision,d.order_code as orderCode,o.code as createdBy,o.name as createdByName , b.created_at as createdAt,b.state," +
        "p.id as paired," +
        "case b.state when '0' then count(distinct  t.product_id) else count(distinct v.product_id) end as category " +
        ",d.buyer_order_code as customerContractNo,b.buyer_comp as buyerCompCode  ,cb.chi_short as buyerCompName," +
        "d.total_price_vat as taxedTotal,d.confirm_total_price_vat as confirmTaxedTotal from sales_contract_base  b " +
        "left join comp_operator o on o.code= b.created_by and o.dc_comp_id = b.created_by_comp " +
        "left join sales_contract_rev d on d.id = b.id and d.revision in (select max(dr.revision) from sales_contract_rev dr where dr.id = d.id) " +
        "left join sales_contract_record_rev v on v.contract_id=d.id and v.revision = d.revision " +
        "left join comp_base cb on  b.buyer_comp = cb.code \n" +
        "left join sales_contract_record_temp t on t.contract_id = d.id " +
        "left join purchase_contract_base p on b.paired_code=p.paired_code" +
        " where b.created_by_comp=?1  and b.state =?2   group by  b.id,b.code,d.revision,d.order_code,o.code,o.name, b.created_at,b.state,p.id,d.buyer_order_code,b.buyer_comp ,cb.chi_short,d.total_price_vat,d.confirm_total_price_vat \n", nativeQuery = true)
    List<SalesContracts> listContracts(String compId, String state);

}
