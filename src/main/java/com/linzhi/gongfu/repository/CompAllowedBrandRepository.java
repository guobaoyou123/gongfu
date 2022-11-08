package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompAllowedBrand;
import com.linzhi.gongfu.entity.CompAllowedBrandId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 操作公司允许经营品牌的Repository
 *
 * @author zgh
 * @create_at 2022-01-21
 */
public interface CompAllowedBrandRepository extends CrudRepository<CompAllowedBrand, CompAllowedBrandId>, QuerydslPredicateExecutor<CompAllowedBrand> {

    /**
     * 查找单位允许经营品牌
     *
     * @param companyCode 单位编码
     * @return 经营品牌列表
     */
    List<CompAllowedBrand> findBrandsByCompAllowedBrandIdCompCode(String companyCode);

    @Modifying
    void deleteAllByCompAllowedBrandId_CompCode(String compId);
}
