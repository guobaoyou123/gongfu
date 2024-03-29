package com.linzhi.gongfu.repository.trade;

import com.linzhi.gongfu.entity.CompBrandOwner;
import com.linzhi.gongfu.entity.CompBrandOwnerId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;


/**
 * 单位拥有品牌Repository
 *
 * @author zgh
 * @create_at 2022-02-07
 */
public interface CompBrandOwnerRepository extends CrudRepository<CompBrandOwner, CompBrandOwnerId>, QuerydslPredicateExecutor<CompBrandOwner> {

}
