package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompAllowedBrand;
import com.linzhi.gongfu.entity.CompAllowedBrandId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompAllowedBrandRepository extends CrudRepository<CompAllowedBrand, CompAllowedBrandId>, QuerydslPredicateExecutor<CompAllowedBrand> {

    /**
     * 查找单位允许经营品牌
     *
     * @param companyCode 单位编码
     * @return
     */
    List<CompAllowedBrand> findBrandsByCompAllowedBrandIdCompCode(String companyCode);
}
