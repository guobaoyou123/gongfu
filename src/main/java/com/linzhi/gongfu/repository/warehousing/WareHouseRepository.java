package com.linzhi.gongfu.repository.warehousing;


import com.linzhi.gongfu.entity.WareHouse;
import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
    List<WareHouse> findWareHouseByCompIdAndState(String companyCode, Availability state);

    /**
     * 查询最大编码
     * @param companyCode 单位编码
     * @return 最大编码
     */
    @Query(value = "select  (cast(max(code) as int)+1) from comp_warehouse where comp_id=?1 ", nativeQuery = true)
    String  findMaxCode(String companyCode);

    /**
     * 更新库房状态
     * @param state 状态
     * @param code 库房编码
     */
    @Modifying
    @Query(value = "UPDATE WareHouse AS w SET w.state=?1 where w.code=?2")
    void updateState(Availability state ,String code);
}
