package com.linzhi.gongfu.repository.trade;

import com.linzhi.gongfu.entity.PurchaseContractRecord;
import com.linzhi.gongfu.entity.PurchaseContractRecordId;
import com.linzhi.gongfu.entity.SalesContractRecord;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 采购合同明细的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface PurchaseContractRecordRepository
    extends CrudRepository<PurchaseContractRecord, PurchaseContractRecordId>, QuerydslPredicateExecutor<PurchaseContractRecord> {

    }
