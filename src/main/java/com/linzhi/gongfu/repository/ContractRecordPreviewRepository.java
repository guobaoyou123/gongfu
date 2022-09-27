package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.ContractRecordPreview;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
/**
 * 采购合同明细预览的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface ContractRecordPreviewRepository extends CrudRepository<ContractRecordPreview, String>, QuerydslPredicateExecutor<ContractRecordPreview> {
    /**
     * 采购合同明细记录预览
     *
     * @param contractId 合同主键
     * @return 明细列表
     */
    @Query(value = " select  c.product_id as product_id ,c.product_code as product_code,\n" +
        "sum(c.previous_my_quantity) as amount,sum(c.my_quantity) as modifiedAmount,\n" +
        "(select sum(re.quantity) from deliver_record re left join deliver_base be   on  re.deliver_code = be.id and be.type='1'where re.product_id = c.product_id and be.contract_id =c.contract_id ) as received,\n" +
        "(select sum(re.actual) from deliver_record re left join deliver_base be   on  re.deliver_code = be.id and be.type='2' where re.product_id = c.product_id and be.contract_id =c.contract_id ) as delivered\n" +
        ",1 as invoicedAmount \n" +
        "from purchase_contract_record_temp c\n" +
        "where c.contract_id=?1 \n" +
        "group by  c.product_id,c.product_code,c.contract_id", nativeQuery = true)
    List<ContractRecordPreview> findPurchaseContractRecordPreview(String contractId);

    /**
     * 销售合同明细记录预览
     *
     * @param contractId 合同主键
     * @return 明细列表
     */
    @Query(value = " select  c.product_id as product_id ,c.product_code as product_code,\n" +
        "sum(c.pre_quantity) as amount,sum(c.quantity) as modifiedAmount,\n" +
        "(select sum(re.quantity) from deliver_record re left join deliver_base be   on  re.deliver_code = be.id and be.type='1'where re.product_id = c.product_id and be.contract_id =c.contract_id ) as received,\n" +
        "(select sum(re.actual) from deliver_record re left join deliver_base be   on  re.deliver_code = be.id and be.type='2' where re.product_id = c.product_id and be.contract_id =c.contract_id ) as delivered\n" +
        ",1 as invoicedAmount \n" +
        "from sales_contract_record_temp c\n" +
        "where c.contract_id=?1 \n" +
        "group by  c.product_id,c.product_code,c.contract_id", nativeQuery = true)
    List<ContractRecordPreview> findSalesContractRecordPreview(String contractId);
}
