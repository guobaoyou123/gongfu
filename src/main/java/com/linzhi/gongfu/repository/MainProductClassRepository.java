package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.BaseProductClassId;
import com.linzhi.gongfu.entity.MainProductClass;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MainProductClassRepository extends CrudRepository<MainProductClass, BaseProductClassId> , QuerydslPredicateExecutor<MainProductClass> {
        List<MainProductClass> findMainProductClassByBaseProductClassId_Type(String type);

}