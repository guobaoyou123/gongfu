package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompAllowedBrand;
import com.linzhi.gongfu.entity.CompAllowedBrandId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface CompAllowedBrandRepository extends CrudRepository<CompAllowedBrand, CompAllowedBrandId>, QuerydslPredicateExecutor<CompAllowedBrand> {
    List<CompAllowedBrand> findBrandsByCompAllowedBrandIdCompCode(String compBuyer);
}
