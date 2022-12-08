package com.linzhi.gongfu.repository.trade;

import com.linzhi.gongfu.entity.SalesContractRecord;
import com.linzhi.gongfu.entity.SalesContractRecordId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 销售合同明细的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface SalesContractRecordRepository
    extends CrudRepository<SalesContractRecord, SalesContractRecordId>, QuerydslPredicateExecutor<SalesContractRecord> {
    }
