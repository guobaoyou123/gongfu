package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.CompTradDetail;
import com.linzhi.gongfu.entity.CompTradId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 交易信息详情内容的Repository
 *
 * @author zgh
 * @create_at 2022-01-21
 */
public interface CompTradDetailRepository extends CrudRepository<CompTradDetail, CompTradId>, QuerydslPredicateExecutor<CompTradDetail> {


}
