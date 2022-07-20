package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompTradBrand;
import com.linzhi.gongfu.entity.CompTradBrandId;
import com.linzhi.gongfu.entity.CompTradeApply;
import com.linzhi.gongfu.enumeration.TradeApply;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CompTradeApplyRepository extends CrudRepository<CompTradeApply, String>, QuerydslPredicateExecutor<CompTradeApply> {
    /**
     * 查询是否有跟某个格友申请采购请求
     * @param handledCompBy 格友编码
     * @param createdCompBy 本单位编码
     * @return 申请采购请求
     */
    Optional<CompTradeApply> findByCreatedCompByAndHandledCompByAndStateAndType(String createdCompBy, String handledCompBy, TradeApply tradeApply,String type);
    /**
     * 查询某个格友最后一次申请采购
     * @param handledCompBy 格友编码
     * @param createdCompBy 本单位编码
     * @return 申请采购请求
     */
    Optional<CompTradeApply> findTopByCreatedCompByAndHandledCompByAndTypeOrderByCreatedAtDesc(String createdCompBy, String handledCompBy,String type);

    List<CompTradeApply> findByHandledCompByAndStateAndTypeOrderByCreatedAtDesc(String companyCode,TradeApply tradeApply,String type);
}
