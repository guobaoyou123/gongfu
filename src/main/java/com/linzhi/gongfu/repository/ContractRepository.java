package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Contract;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ContractRepository
    extends CrudRepository<Contract, String>, QuerydslPredicateExecutor<Contract> {
}
