package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.ContractRevision;
import com.linzhi.gongfu.entity.ContractRevisionId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;


public interface ContractRevisionRepository
    extends CrudRepository<ContractRevision, ContractRevisionId>, QuerydslPredicateExecutor<ContractRevision> {


}
