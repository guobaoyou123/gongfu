package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompTradeApply;
import com.linzhi.gongfu.enumeration.TradeApply;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
/**
 * 申请采购详情信息的Repository
 *
 * @author zgh
 * @create_at 2022-01-21
 */
public interface CompTradeApplyRepository extends CrudRepository<CompTradeApply, String>, QuerydslPredicateExecutor<CompTradeApply> {
    /**
     * 查询是否有跟某个格友申请采购请求
     *
     * @param handledCompBy 格友编码
     * @param createdCompBy 本单位编码
     * @return 申请采购请求
     */
    Optional<CompTradeApply> findByCreatedCompByAndHandledCompByAndStateAndType(String createdCompBy, String handledCompBy, TradeApply tradeApply, String type);

    /**
     * 查询某个格友最后一次申请采购
     *
     * @param handledCompBy 格友编码
     * @param createdCompBy 本单位编码
     * @return 申请采购请求
     */
    Optional<CompTradeApply> findTopByCreatedCompByAndHandledCompByAndTypeOrderByCreatedAtDesc(String createdCompBy, String handledCompBy, String type);

    /**
     * 待处理申请列表
     *
     * @param companyCode 本公司编码
     * @param tradeApply  待申请
     * @param type        申请采购
     * @return 待处理列表
     */
    @Cacheable(value = "trade_apply_List;1800", key = "#companyCode+'-'+#type")
    List<CompTradeApply> findByHandledCompByAndStateAndTypeOrderByCreatedAtDesc(String companyCode, TradeApply tradeApply, String type);

    /**
     * 查询历史记录
     *
     * @param companyCode 单位编码
     * @return 历史记录列表
     */
    @Cacheable(value = "trade_apply_history_List;1800", key = "#companyCode")
    @Query(value = "select * from comp_trade_apply\n" +
        "where created_comp_by=?1 or(handled_comp_by=?1 and state<>'0')", nativeQuery = true)
    List<CompTradeApply> listApplyHistories(String companyCode);

    /**
     * 查询申请记录详情
     *
     * @param id 申请记录编码
     * @return 申请记录详情
     */

    Optional<CompTradeApply> findById(String id);
}
