package com.linzhi.gongfu.repository.warehousing;


import com.linzhi.gongfu.entity.WareHouse;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
/**
 * 操作库房信息内容的Repository
 *
 * @author zgh
 * @create_at 2022-12-09
 */
public interface WareHouseRepository extends CrudRepository<WareHouse, String>, QuerydslPredicateExecutor<WareHouse> {

}
