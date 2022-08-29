package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.DeliverTemp;
import com.linzhi.gongfu.entity.DeliverTempId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DeliveryTempRepository extends CrudRepository<DeliverTemp, DeliverTempId>, QuerydslPredicateExecutor<DeliverTemp> {

    void deleteDeliverTempsByDeliverTempId_ContractId(String contractId);

    List<DeliverTemp> findDeliverTempsByDeliverTempId_ContractId(String contractId);
}
