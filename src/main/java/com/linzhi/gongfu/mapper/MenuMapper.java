package com.linzhi.gongfu.mapper;

import java.util.Set;

import com.linzhi.gongfu.dto.TMenu;
import com.linzhi.gongfu.entity.MainMenu;
import com.linzhi.gongfu.entity.SubMenu;
import com.linzhi.gongfu.vo.VPreloadMenuResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换前端菜单结构
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Mapper(componentModel = "spring", uses = { SceneMapper.class })
public interface MenuMapper {
    TMenu toDTO(MainMenu menu);

    @Mapping(target = "children", ignore = true)
    TMenu toDTO(SubMenu menu);

    Set<TMenu> toMainMenuDTOs(Set<MainMenu> menus);

    Set<TMenu> toSubMenuDTOs(Set<SubMenu> menus);

    @Mapping(target = "scene", source = "scene.code")
    VPreloadMenuResponse.VMainMenu toPreloadMainMenu(TMenu menu);

    @Mapping(target = "scene", source = "scene.code")
    VPreloadMenuResponse.VSubMenu toPreloadSubMenu(TMenu menu);

    Set<VPreloadMenuResponse.VSubMenu> toPreloadSubMenus(Set<TMenu> menus);
}
