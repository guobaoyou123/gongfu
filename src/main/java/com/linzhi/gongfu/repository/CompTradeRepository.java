package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.dto.TEnrolledSupplier;
import com.linzhi.gongfu.entity.CompTrad;
import com.linzhi.gongfu.entity.CompTradId;
import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 查询供应Repository
 *
 * @author zgh
 * @create_at 2022-01-27
 */
public interface CompTradeRepository
    extends CrudRepository<CompTrad, CompTradId>, QuerydslPredicateExecutor<CompTrad> {
    List<CompTrad> findSuppliersByCompTradId_CompBuyerAndState(@Param("compBuyer") String compBuyer,@Param("state") Availability state);


    List<CompTrad> findCompTradsByCompTradId_CompBuyerAndCompTradId_CompSalerIn(String compBuyer,List<String> compSuppliers);


    /**
     * 更改买方所属操作员
     * @param operators 操作员编码（以逗号隔开）
     * @param compTradId 主键
     */
    @Modifying
    @Query("UPDATE CompTrad  c set c.buyerBelongTo=?1 where c.compTradId=?2")
    void updateCompTrade(String operators,CompTradId compTradId);

    /**
     * 更改交易信息状态
     * @param state 状态
     * @param compBuyer 买方单位编码
     * @param compSalers 卖方单位编码列表
     */
    @Modifying
    @Query("UPDATE CompTrad  c set c.state=?1 where c.compTradId.compBuyer=?2 and c.compTradId.compSaler in ?3")
    void updateCompTradeState(Availability state,String compBuyer,List<String> compSalers);
}
