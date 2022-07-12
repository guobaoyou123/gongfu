package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.OperatorDetail;
import com.linzhi.gongfu.entity.OperatorId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 公司操作员Repository
 *
 * @author xutao
 * @create_at 2021-12-23
 */
public interface OperatorDetailRepository extends CrudRepository<OperatorDetail, OperatorId>, QuerydslPredicateExecutor<OperatorDetail> {

}
