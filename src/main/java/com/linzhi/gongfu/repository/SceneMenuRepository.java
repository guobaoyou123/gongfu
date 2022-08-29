package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.SceneMenu;
import com.linzhi.gongfu.entity.SceneMenuId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SceneMenuRepository extends CrudRepository<SceneMenu, SceneMenuId>, QuerydslPredicateExecutor<SceneMenu> {

    @Query(value = "select distinct  m.* from dc_scene_menu  m\n" +
        "where m.menu_code in (select  code  from sys_menu where parent = (select code from sys_menu where name=?1)) ",
        nativeQuery = true)
    List<SceneMenu> findList(String menuName);
}
