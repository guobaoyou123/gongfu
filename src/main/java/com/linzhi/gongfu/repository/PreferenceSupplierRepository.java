package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.PreferenceSupplier;
import com.linzhi.gongfu.entity.PreferenceSupplierId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface PreferenceSupplierRepository extends CrudRepository<PreferenceSupplier, PreferenceSupplierId>, QuerydslPredicateExecutor<PreferenceSupplier> {

}
