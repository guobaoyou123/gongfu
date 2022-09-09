package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.PurchaseContractRevision;
import com.linzhi.gongfu.entity.PurchaseContractRevisionId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;


public interface PurchaseContractRevisionRepository
    extends CrudRepository<PurchaseContractRevision, PurchaseContractRevisionId>, QuerydslPredicateExecutor<PurchaseContractRevision> {


}
