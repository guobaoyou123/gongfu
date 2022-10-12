package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.CompTradBase;
import com.linzhi.gongfu.entity.CompTradeId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 交易信息详情内容的Repository
 *
 * @author zgh
 * @create_at 2022-01-21
 */
public interface CompTradeDetailRepository extends CrudRepository<CompTradBase, CompTradeId>, QuerydslPredicateExecutor<CompTradBase> {


}
