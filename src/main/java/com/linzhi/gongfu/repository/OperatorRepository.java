package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.DOperator;
import org.springframework.data.repository.CrudRepository;

/**
 * 公司操作员Repository
 * @author xutao
 * @create_at 2021-12-23
 */
public interface OperatorRepository extends CrudRepository<String, DOperator> {
}
