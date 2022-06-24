package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.ContractReceived;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContractReceivedRepository  extends CrudRepository<ContractReceived, String>, QuerydslPredicateExecutor<ContractReceived> {


    @Query(value = "  select  r.product_id as id ,r.product_code as code,r.product_description as describe,r.my_charge_unit as charge_unit, \n" +
        "sum(r.my_actual)as delivered ,\n" +
        "(select sum(my_quantity) from  contract_record_temp t where t.product_id =r.product_id and  t.contract_id=b.contract_id ) as amount,\n" +
        "(select sum(re.my_quantity) from deliver_record re\n" +
                "left join deliver_base be   on  re.deliver_code = be.id and be.type='2'\n" +

               " where re.product_id = r.product_id and be.contract_id = b.contract_id   ) as received\n" +
        " from deliver_record r\n" +
        " left join deliver_base b  on r.deliver_code = b.id and b.type='1' \n" +

        "where  r.type='1' and b.contract_id=?1 \n" +
        "        group by  r.product_id,r.product_code,r.product_description ,r.my_charge_unit,b.contract_id",nativeQuery = true)
    List<ContractReceived> findContractReceivedList(String contractId);
}
