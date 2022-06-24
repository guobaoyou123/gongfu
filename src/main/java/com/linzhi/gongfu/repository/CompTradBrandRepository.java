package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompTradBrand;
import com.linzhi.gongfu.entity.CompTradBrandId;
import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompTradBrandRepository extends CrudRepository<CompTradBrand, CompTradBrandId> , QuerydslPredicateExecutor<CompTradBrand> {

    List<CompTradBrand> findCompTradBrandByCompTradBrandId_BrandCodeInAndCompTradBrandId_CompBuyerAndCompany_StateOrderBySortDesc(List<String> brandCode, String compBuyer, Availability state);

    @Modifying
    @Query(value="delete comp_trade_brand  where comp_buyer=?1 and comp_saler=?2",nativeQuery = true)
    void  deleteCompTradBrand(String compBuyer,String compSupplier);
}
