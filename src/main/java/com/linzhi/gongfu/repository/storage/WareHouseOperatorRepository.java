package com.linzhi.gongfu.repository.storage;

import com.linzhi.gongfu.entity.WareHouseOperator;
import com.linzhi.gongfu.entity.WareHouseOperatorId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface WareHouseOperatorRepository extends CrudRepository<WareHouseOperator, WareHouseOperatorId>, QuerydslPredicateExecutor<WareHouseOperator> {

    void deleteWareHouseOperatorByWareHouseOperatorId_CodeAndWareHouseOperatorId_CompId(String wareHouseCode,String compId);
}
