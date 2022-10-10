package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.AdministrativeArea;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 操作行政区划内容的Repository
 *
 * @author zgh
 * @create_at 2022-01-21
 */

public interface AdministrativeAreaRepository extends CrudRepository<AdministrativeArea, String>, QuerydslPredicateExecutor<AdministrativeArea> {


}
