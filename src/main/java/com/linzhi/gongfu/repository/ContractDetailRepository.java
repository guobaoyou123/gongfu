package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.ContractDetail;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ContractDetailRepository
    extends CrudRepository<ContractDetail, String>, QuerydslPredicateExecutor<ContractDetail> {
}
