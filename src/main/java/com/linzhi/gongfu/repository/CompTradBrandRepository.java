package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompTradBrand;
import com.linzhi.gongfu.entity.CompTradBrandId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface CompTradBrandRepository extends CrudRepository<CompTradBrand, CompTradBrandId> , QuerydslPredicateExecutor<CompTradBrand> {
  List<CompTradBrand> findCompTradBrandByCompTradBrandId_BrandCodeInAndCompTradBrandId_CompBuyerOrderBySortDesc(List<String> brandCode,String compBuyer);

}
