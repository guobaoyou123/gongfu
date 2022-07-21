package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.Blacklist;
import com.linzhi.gongfu.entity.BlacklistId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface BlacklistRepository extends CrudRepository<Blacklist, BlacklistId>, QuerydslPredicateExecutor<Blacklist> {

}
