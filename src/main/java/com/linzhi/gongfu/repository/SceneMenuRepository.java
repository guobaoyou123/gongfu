package com.linzhi.gongfu.repository;


import com.linzhi.gongfu.entity.SceneMenu;
import com.linzhi.gongfu.entity.SceneMenuId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
/**
 * 场景菜单的Repository
 *
 * @author zgh
 * @create_at 2022-09-20
 */
public interface SceneMenuRepository extends CrudRepository<SceneMenu, SceneMenuId>, QuerydslPredicateExecutor<SceneMenu> {
    /**
     * 场景菜单列表
     *
     * @param menuName 菜单名称
     * @return 场景菜单列表
     */
    @Query(value = "select distinct  m.* from dc_scene_menu  m\n" +
        "where m.menu_code in (select  code  from sys_menu where parent = (select code from sys_menu where name=?1)) ",
        nativeQuery = true)
    List<SceneMenu> findList(String menuName);
}
