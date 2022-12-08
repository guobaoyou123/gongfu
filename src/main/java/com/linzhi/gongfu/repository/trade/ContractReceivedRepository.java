package com.linzhi.gongfu.repository.trade;

import com.linzhi.gongfu.entity.ContractReceived;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 合同已(s收)发产品的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface ContractReceivedRepository extends CrudRepository<ContractReceived, String>, QuerydslPredicateExecutor<ContractReceived> {

    /**
     * 合同产品明细已收货列表
     *
     * @param contractId 合同主键
     * @return 合同产品明细已收（发）货列表
     */
    @Query(value = "  select  r.product_id as id ,r.product_code as code,r.product_description as describe,r.charge_unit as charge_unit, \n" +
        "sum(r.quantity)as delivered ,\n" +
        "(select sum(my_quantity) from  purchase_contract_record_temp t where t.product_id =r.product_id and  t.contract_id=b.contract_id ) as amount,\n" +
        "(select sum(re.actual) from deliver_record re\n" +
        "left join deliver_base be   on  re.deliver_code = be.id and be.type='1'\n" +

        " where re.product_id = r.product_id and be.contract_id = b.contract_id   ) as received\n" +
        " from deliver_record r\n" +
        " left join deliver_base b  on r.deliver_code = b.id and b.type='1' \n" +

        "where  r.type='2' and b.contract_id=?1 \n" +
        "        group by  r.product_id,r.product_code,r.product_description ,r.charge_unit,b.contract_id", nativeQuery = true)
    List<ContractReceived> findContractReceivedList(String contractId);

    /**
     * 合同产品明细已发货列表
     *
     * @param contractId 合同主键
     * @return 合同产品明细已收（发）货列表
     */
    @Query(value = "  select  r.product_id as id ,r.product_code as code,r.product_description as describe,r.charge_unit as charge_unit, \n" +
        "sum(r.quantity)as delivered ,\n" +
        "(select sum(quantity) from  sales_contract_record_temp t where t.product_id =r.product_id and  t.contract_id=b.contract_id ) as amount,\n" +
        "(select sum(re.actual) from deliver_record re\n" +
        "left join deliver_base be   on  re.deliver_code = be.id and be.type='1'\n" +

        " where re.product_id = r.product_id and be.contract_id = b.contract_id   ) as received\n" +
        " from deliver_record r\n" +
        " left join deliver_base b  on r.deliver_code = b.id and b.type='1' \n" +

        "where  r.type='2' and b.contract_id=?1 \n" +
        "        group by  r.product_id,r.product_code,r.product_description ,r.charge_unit,b.contract_id", nativeQuery = true)
    List<ContractReceived> findContractDeliveredList(String contractId);
}
