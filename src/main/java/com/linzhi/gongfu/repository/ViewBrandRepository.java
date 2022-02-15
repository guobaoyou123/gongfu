package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.ViewBrand;
import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface ViewBrandRepository extends CrudRepository<ViewBrand,String>, QuerydslPredicateExecutor<ViewBrand> {
  Set<ViewBrand> findViewBrandByClass2AndStateOrderBySortDescCodeAsc(String class2, Availability state);
}
