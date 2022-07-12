package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.CompVisible;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
/**
 * 查询公司设置可见内容Repository
 *
 * @author zgh
 * @create_at 2022-07-12
 */
public interface CompVisibleRepository   extends CrudRepository<CompVisible, String>, QuerydslPredicateExecutor<CompVisible> {
}
