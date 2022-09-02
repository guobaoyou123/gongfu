package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TMenu;
import com.linzhi.gongfu.entity.MainMenu;
import com.linzhi.gongfu.entity.SubMenu;
import com.linzhi.gongfu.vo.VPreloadMenuResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

/**
 * 用于转换前端菜单结构
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Mapper(componentModel = "spring", uses = {SceneMapper.class})
public interface MenuMapper {
    @Mapping(target = "scenes", source = "scenes")
    TMenu toDTO(MainMenu menu);

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "scenes", source = "scenes")
    TMenu toDTO(SubMenu menu);

    Set<TMenu> toSubMenuDTOs(Set<SubMenu> menus);

    @Mapping(target = "scene", expression = "java(menu.getScenes().stream().map(tScene -> tScene.getCode()).toList())")
    VPreloadMenuResponse.VMainMenu toPreloadMainMenu(TMenu menu);

    @Mapping(target = "scene", expression = "java(menu.getScenes().stream().map(tScene -> tScene.getCode()).toList())")
    VPreloadMenuResponse.VSubMenu toPreloadSubMenu(TMenu menu);

    Set<VPreloadMenuResponse.VSubMenu> toPreloadSubMenus(Set<TMenu> menus);
}
