package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.CompTaxModel;
import com.linzhi.gongfu.entity.CompTradId;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface CompTaxModelRepository  extends CrudRepository<CompTaxModel, CompTradId>, QuerydslPredicateExecutor<CompTaxModel> {
}
