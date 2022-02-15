package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.converter.EnrollmentConverter;
import com.linzhi.gongfu.entity.CompTrad;
import com.linzhi.gongfu.entity.CompTradId;
import com.linzhi.gongfu.entity.OperatorId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

/**
 * 查询供应Repository
 *
 * @author zgh
 * @create_at 2022-01-27
 */
public interface CompTradeRepository
    extends CrudRepository<CompTrad, CompTradId>, QuerydslPredicateExecutor<CompTrad> {
    Page<CompTrad> findSuppliersByCompTradIdCompBuyer(@Param("compBuyer") String compBuyer, Pageable pageable);



}
