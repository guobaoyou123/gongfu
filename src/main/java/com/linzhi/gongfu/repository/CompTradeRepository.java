package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.CompTrad;
import com.linzhi.gongfu.entity.CompTradId;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.TaxMode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 交易信息的Repository
 *
 * @author zgh
 * @create_at 2022-01-27
 */
public interface CompTradeRepository
    extends CrudRepository<CompTrad, CompTradId>, QuerydslPredicateExecutor<CompTrad> {

    /**
     * 查找供应商列表
     *
     * @param compBuyer 买方编码
     * @param state     状态
     * @return 供应商列表
     */
    List<CompTrad> findSuppliersByCompTradId_CompBuyerAndState(@Param("compBuyer") String compBuyer, @Param("state") Availability state);

    /**
     * 查找交易列表
     *
     * @param compBuyer     买方编码
     * @param compSuppliers 卖方编码列表
     * @return 交易列表
     */
    List<CompTrad> findCompTradsByCompTradId_CompBuyerAndCompTradId_CompSalerIn(String compBuyer, List<String> compSuppliers);

    /**
     * 更改买方所属操作员
     *
     * @param operators  操作员编码（以逗号隔开）
     * @param compTradId 主键
     */

    @Modifying
    @Query("UPDATE CompTrad  c set c.buyerBelongTo=?1 where c.compTradId=?2")
    void updateCompTradeBuyer(String operators, CompTradId compTradId);

    /**
     * 更改交易信息状态
     *
     * @param state      状态
     * @param compBuyer  买方单位编码
     * @param compSalers 卖方单位编码列表
     */
    @Modifying
    @Query("UPDATE CompTrad  c set c.state=?1 where c.compTradId.compBuyer=?2 and c.compTradId.compSaler in ?3")
    void updateCompTradeState(Availability state, String compBuyer, List<String> compSalers);

    /**
     * 更改交易信息状态
     *
     * @param state      状态
     * @param compBuyers 买方单位编码列表
     * @param compSaler  卖方单位编码
     */
    @Modifying
    @Query("UPDATE CompTrad  c set c.state=?1 where c.compTradId.compBuyer in ?2 and c.compTradId.compSaler = ?3")
    void updateCompTradeState(Availability state, List<String> compBuyers, String compSaler);

    /**
     * 更改卖方所属操作员
     *
     * @param operators  操作员编码（以逗号隔开）
     * @param compTradId 主键
     */
    @Modifying
    @Query("UPDATE CompTrad  c set c.salerBelongTo=?1 where c.compTradId=?2")
    void updateCompTradeSaler(String operators, CompTradId compTradId);

    /**
     * 更新交易中的报价模式
     *
     * @param taxMode    报价模式
     * @param compTradId 交易主键
     */
    @Modifying
    @Query("UPDATE CompTrad  c set c.taxModel=?1 where c.compTradId=?2")
    void updateTaxModel(TaxMode taxMode, CompTradId compTradId);
}
