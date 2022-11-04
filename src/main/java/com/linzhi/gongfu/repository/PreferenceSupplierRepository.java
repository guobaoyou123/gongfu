package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PreferenceSupplier;
import com.linzhi.gongfu.entity.PreferenceSupplierId;
import org.mapstruct.Mapping;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PreferenceSupplierRepository extends CrudRepository<PreferenceSupplier, PreferenceSupplierId>, QuerydslPredicateExecutor<PreferenceSupplier> {

    @Modifying
    void deleteByPreferenceSupplierId_CompCodeAndPreferenceSupplierId_BrandCode(String companyCode,String brandCode);
}
