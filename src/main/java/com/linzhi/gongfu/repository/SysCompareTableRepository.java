package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.SysCompareTable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface SysCompareTableRepository extends CrudRepository<SysCompareTable,String> , QuerydslPredicateExecutor<SysCompareTable> {
     SysCompareTable findSysCompareTableByName(String name);
}
