package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.TaxRates;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.VatRateType;
import com.linzhi.gongfu.enumeration.Whether;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TaxRatesRepository extends CrudRepository<TaxRates,String>, QuerydslPredicateExecutor<TaxRates> {

    Optional<TaxRates> findByTypeAndDeFlagAndUseCountry(VatRateType type, Whether deFlag, String userCountry);

    List<TaxRates> findTaxRatesByUseCountryAndTypeAndState(String userCountry, VatRateType type, Availability state);
}
