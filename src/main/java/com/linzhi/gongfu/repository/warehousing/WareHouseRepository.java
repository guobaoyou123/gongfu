package com.linzhi.gongfu.repository.warehousing;


import com.linzhi.gongfu.entity.WareHouse;
import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 操作库房信息内容的Repository
 *
 * @author zgh
 * @create_at 2022-12-09
 */
public interface WareHouseRepository extends CrudRepository<WareHouse, String>, QuerydslPredicateExecutor<WareHouse> {

    /**
     * 查找本单位所有库房
     * @param companyCode 单位编码
     * @param state 状态
     * @return 库房列表
     */
    List<WareHouse> findWareHouseByCompIdAndSate(String companyCode, Availability state);
}
