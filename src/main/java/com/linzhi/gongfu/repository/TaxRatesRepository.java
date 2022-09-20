package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.TaxRates;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.VatRateType;
import com.linzhi.gongfu.enumeration.Whether;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
/**
 * 税率的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface TaxRatesRepository extends CrudRepository<TaxRates, String>, QuerydslPredicateExecutor<TaxRates> {
    /**
     * 税率详情
     *
     * @param type        类型
     * @param deFlag      是否默认
     * @param userCountry 国家
     * @return 税率详情
     */
    Optional<TaxRates> findByTypeAndDeFlagAndUseCountry(VatRateType type, Whether deFlag, String userCountry);

    /**
     * 税率列表
     *
     * @param userCountry 国家
     * @param type        类型
     * @param state       状态
     * @return 税率列表
     */
    List<TaxRates> findTaxRatesByUseCountryAndTypeAndState(String userCountry, VatRateType type, Availability state);
}
