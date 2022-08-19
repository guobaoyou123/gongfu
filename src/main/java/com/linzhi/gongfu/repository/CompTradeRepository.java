package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.CompTrad;
import com.linzhi.gongfu.entity.CompTradId;
import com.linzhi.gongfu.enumeration.Availability;
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


    Optional<CompTrad> findCompTradsByCompTradId_CompBuyerAndCompTradId_CompSaler(String comBuyer,String compSaler);

}
