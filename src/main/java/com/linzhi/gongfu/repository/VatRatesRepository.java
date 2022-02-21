package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.VatRates;
import com.linzhi.gongfu.enumeration.VatRateType;
import com.linzhi.gongfu.enumeration.Whether;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VatRatesRepository extends CrudRepository<VatRates,String>, QuerydslPredicateExecutor<VatRates> {

    Optional<VatRates> findByTypeAndDeflagAndUseCountry(VatRateType type, Whether deflag,String userCountry);
}
