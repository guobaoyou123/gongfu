package com.linzhi.gongfu.repository.trade;

import com.linzhi.gongfu.entity.CompTaxModel;
import com.linzhi.gongfu.entity.CompTradeId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 交易税模式的Repository
 *
 * @author zgh
 * @create_at 2022-01-21
 */
public interface CompTaxModelRepository extends CrudRepository<CompTaxModel, CompTradeId>, QuerydslPredicateExecutor<CompTaxModel> {
}
