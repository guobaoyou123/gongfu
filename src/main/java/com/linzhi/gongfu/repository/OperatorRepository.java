package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Operator;
import com.linzhi.gongfu.entity.OperatorId;

import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 公司操作员Repository
 *
 * @author xutao
 * @create_at 2021-12-23
 */
public interface OperatorRepository extends CrudRepository<Operator, OperatorId>, QuerydslPredicateExecutor<Operator> {

     List<Operator> findOperatorByStateAndIdentity_CompanyCodeAndIdentity_OperatorCodeNot(Availability state, String companyCode,String operator);
}
