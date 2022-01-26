package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.MainMenu;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 前端界面一级菜单Repository
 *
 * @author xutao
 * @create_at 2022-01-20
 */
public interface MainMenuRepository extends CrudRepository<MainMenu, String>, QuerydslPredicateExecutor<MainMenu> {
}
