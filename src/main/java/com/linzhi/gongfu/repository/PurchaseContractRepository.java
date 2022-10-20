package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PurchaseContractList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 采购合同；列表的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface PurchaseContractRepository
    extends CrudRepository<PurchaseContractList, String>, QuerydslPredicateExecutor<PurchaseContractList> {
    /**
     * 查找有相同单位合同号的合同数量
     *
     * @param dcCompId  本单位编码
     * @param orderCode 单位合同号
     * @return 合同数量
     */
    @Query(value = "select  count(distinct c.id) " +
        "from purchase_contract_base c ,purchase_contract_rev  r  " +
        "where  c.created_by_comp=?1 and c.id = r.id and r.order_code=?2  " +
        "  and r.revision = (select max(revision) from purchase_contract_rev re where re.id=r.id) " +
        "  and  c.state ='1' ",
        nativeQuery = true)
    int findByOrderCode(String dcCompId, String orderCode);

    /**
     * 查找有相同单位合同号的合同数量
     *
     * @param dcCompId   本单位编码
     * @param orderCode  单位合同号
     * @param contractId 合同主键
     * @return
     */
    @Query(value = "select  count(distinct c.id) from purchase_contract_base c ,purchase_contract_rev  r " +
        " where  c.created_by_comp=?1 and c.id = r.id and r.order_code=?2  " +
        "  and r.revision = (select max(revision) from purchase_contract_rev re where re.id=r.id) " +
        "  and  c.state ='1' " +
        " and c.id <> ?3",
        nativeQuery = true)
    int findByOrderCode(String dcCompId, String orderCode, String contractId);

    /**
     * 根据指纹查找合同编码列表
     *
     * @param dcCompId     本单位编码
     * @param sequenceCode 指纹
     * @return 合同编码列表
     */
    @Query(value = "select  c.id from purchase_contract_base c ,purchase_contract_rev  r  where  c.created_by_comp =?1   and c.id = r.id   and r.fingerprint =?2 and r.revision=(select max(revision) from purchase_contract_rev v where v.id = r.id) ", nativeQuery = true)
    List<String> findContractId(String dcCompId, String sequenceCode);

    /**
     * 合同列表
     *
     * @param compId   单位编码
     * @param operator 操作员
     * @param state    状态
     * @return 合同列表
     */
    @Query(value = "select b.*,o.name as createdByName ,c.code as salesContractCode,r.order_code as salesOrderCode,d.order_code as order_code ,d.revision as revision,d.saler_order_code as supplierContractNo ," +
        "case b.state when '0' then count(distinct t.product_id)\n" +
        "else  count(distinct v.product_id)\n" +
        "end as category,\n" +
        "d.confirm_total_price_vat as confirmTaxedTotal, \n" +
        " d.total_price_vat  as taxedTotal,\n" +
        " cb.chi_short  as salerCompNameShort, p.paired_code as paired\n" +
        " from   purchase_contract_base b\n" +
        "left join comp_operator o on b.created_by_comp = o.dc_comp_id and b.created_by = o.code\n" +
        "left join comp_base cb on  b.saler_comp = cb.code \n" +
        "left join sales_contract_base c on c.id = b.sales_contract_id\n" +
        "left join sales_contract_rev r on r.id = b.sales_contract_id  and r.revision in (select max(revision) from sales_contract_rev  where id = r.id)\n" +
        "left join purchase_contract_rev d on d.id = b.id  and d.revision in (select max(revision) from purchase_contract_rev  where id = d.id)\n" +
        "left join purchase_contract_record_temp t on t.contract_id = d.id\n" +
        "left join purchase_contract_record_rev v on v.contract_id = d.id and v.revision = d.revision\n" +
        "left join sales_contract_base p on b.paired_code=p.paired_code\n "+
        " where  b.created_by_comp=?1 and b.created_by=?2  and b.state=?3 \n" +
        "group by b.id,o.name \n" +
        "      ,b.code\n" +
        "      ,b.sales_contract_id\n" +
        "      ,b.created_by_comp\n" +
        "      ,b.created_by\n" +
        "      ,b.buyer_comp\n" +
        "      ,b.buyer_comp_name\n" +
        "      ,b.saler_comp\n" +
        "      ,b.saler_comp_name\n" +
        "      ,b.created_at\n" +
        "      ,b.paired_code\n" +
        "      ,b.state ,c.code ,r.order_code ,d.order_code,d.saler_order_code ,d.revision,  \n" +
        "   d.confirm_total_price_vat,d.total_price_vat,cb.chi_short,p.paired_code\n" +
        "order by b.created_at desc,cast(RIGHT(b.code,3) as int )  desc ",
        nativeQuery = true)
    List<PurchaseContractList> listContracts(String compId, String operator, String state);

    /**
     * 合同列表
     *
     * @param compId 单位编码
     * @param state  状态
     * @return 合同列表
     */
    @Query(value = "select b.*,o.name as createdByName ,c.code as salesContractCode,r.order_code as salesOrderCode,d.order_code as order_code,d.revision as revision ,d.saler_order_code as supplierContractNo ," +
        "case b.state when '0' then count(distinct t.product_id)\n" +
        "else  count(distinct v.product_id)\n" +
        "end as category,\n" +
        "d.confirm_total_price_vat as confirmTaxedTotal, \n" +
        " d.total_price_vat  as taxedTotal,\n" +
        " cb.chi_short  as salerCompNameShort, p.paired_code as paired\n" +
        " from   purchase_contract_base b\n" +
        "left join comp_operator o on b.created_by_comp = o.dc_comp_id and b.created_by = o.code\n" +
        "left join comp_base cb on  b.saler_comp = cb.code\n" +
        "left join sales_contract_base c on c.id = b.sales_contract_id\n" +
        "left join sales_contract_rev r on r.id = b.sales_contract_id  and r.revision in (select max(revision) from sales_contract_rev  where id = r.id)\n" +
        "left join purchase_contract_rev d on d.id = b.id  and d.revision in (select max(revision) from purchase_contract_rev  where id = d.id)\n" +
        "left join purchase_contract_record_temp t on t.contract_id = d.id\n" +
        "left join purchase_contract_record_rev v on v.contract_id = d.id and v.revision = d.revision\n" +
        "left join sales_contract_base p on b.paired_code=p.paired_code\n "+
        " where  b.created_by_comp=?1   and b.state=?2 \n" +
        "group by b.id,o.name \n" +
        "      ,b.code\n" +
        "      ,b.sales_contract_id\n" +
        "      ,b.created_by_comp\n" +
        "      ,b.created_by\n" +
        "      ,b.buyer_comp\n" +
        "      ,b.buyer_comp_name\n" +
        "      ,b.saler_comp\n" +
        "      ,b.saler_comp_name\n" +
        "      ,b.created_at\n" +
        "      ,b.paired_code\n" +
        "      ,b.state ,c.code ,r.order_code ,d.order_code ,d.saler_order_code,d.revision,  \n" +
        "   d.confirm_total_price_vat,d.total_price_vat,cb.chi_short,p.paired_code\n" +
        "order by b.created_at desc,cast(RIGHT(b.code,3) as int )  desc ",
        nativeQuery = true)
    List<PurchaseContractList> listContracts(String compId, String state);
}
