package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Operator;
import com.linzhi.gongfu.entity.OperatorId;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 公司操作员Repository
 *
 * @author xutao
 * @create_at 2021-12-23
 */
public interface OperatorRepository extends CrudRepository<Operator, OperatorId>, QuerydslPredicateExecutor<Operator> {
}
