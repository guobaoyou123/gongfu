package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.BaseProductClassId;
import com.linzhi.gongfu.entity.MainProductClass;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MainProductClassRepository extends CrudRepository<MainProductClass, BaseProductClassId>, QuerydslPredicateExecutor<MainProductClass> {
    /**
     * 查找产品分类
     *
     * @param type 类型
     * @return 分类列表
     */
    List<MainProductClass> findMainProductClassByBaseProductClassId_Type(String type);

}
