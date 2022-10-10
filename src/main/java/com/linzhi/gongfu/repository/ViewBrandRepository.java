package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.ViewBrand;
import com.linzhi.gongfu.enumeration.Availability;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

/**
 * 品牌和二级分类视图表的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface ViewBrandRepository extends CrudRepository<ViewBrand, String>, QuerydslPredicateExecutor<ViewBrand> {
    Set<ViewBrand> findViewBrandByClass2AndStateOrderBySortDescCodeAsc(String class2, Availability state);
}
