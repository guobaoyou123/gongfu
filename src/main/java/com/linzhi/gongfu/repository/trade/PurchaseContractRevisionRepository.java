package com.linzhi.gongfu.repository.trade;


import com.linzhi.gongfu.entity.PurchaseContractRevision;
import com.linzhi.gongfu.entity.PurchaseContractRevisionId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 采购合同版本的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface PurchaseContractRevisionRepository
    extends CrudRepository<PurchaseContractRevision, PurchaseContractRevisionId>, QuerydslPredicateExecutor<PurchaseContractRevision> {


}
