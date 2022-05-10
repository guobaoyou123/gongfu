package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.converter.EnrollmentConverter;
import com.linzhi.gongfu.entity.CompTrad;
import com.linzhi.gongfu.entity.CompTradId;
import com.linzhi.gongfu.entity.OperatorId;
import com.linzhi.gongfu.enumeration.Trade;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
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
    Page<CompTrad> findSuppliersByCompTradIdCompBuyerAndState(@Param("compBuyer") String compBuyer, Pageable pageable,@Param("state") Trade state);

    List<CompTrad> findSuppliersByCompTradId_CompBuyerAndState(@Param("compBuyer") String compBuyer,@Param("state") Trade state);


    List<CompTrad> findCompTradsByCompTradId_CompBuyerAndCompTradId_CompSalerIn(String compBuyer,List<String> compSalers);


}
