package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.CompTradDetail;
import com.linzhi.gongfu.entity.CompTradId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface CompTradDetailRepository extends CrudRepository<CompTradDetail, CompTradId>, QuerydslPredicateExecutor<CompTradDetail> {


}
