package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompTradeBrand;
import com.linzhi.gongfu.entity.CompTradeBrandId;
import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 交易品牌的Repository
 *
 * @author zgh
 * @create_at 2022-01-21
 */
public interface CompTradeBrandRepository extends CrudRepository<CompTradeBrand, CompTradeBrandId>, QuerydslPredicateExecutor<CompTradeBrand> {
    /**
     * 根据品牌查找供应商
     *
     * @param brandCode 品牌编码
     * @param compBuyer 买方单位编码
     * @param state     状态
     * @return 供应商列表
     */
    List<CompTradeBrand> findCompTradeBrandByCompTradeBrandId_BrandCodeInAndCompTradeBrandId_CompBuyerAndCompany_StateOrderBySortDesc(List<String> brandCode, String compBuyer, Availability state);

    /**
     * 删除交易品牌
     *
     * @param compBuyer    买方
     * @param compSupplier 卖方
     */
    void deleteCompTradeBrandByCompTradeBrandId_CompBuyerAndAndCompTradeBrandId_CompSaler(String compBuyer, String compSupplier);

}
