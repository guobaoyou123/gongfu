package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TMenu;
import com.linzhi.gongfu.dto.TScene;
import com.linzhi.gongfu.entity.MainMenu;
import com.linzhi.gongfu.mapper.MenuMapper;
import com.linzhi.gongfu.repository.MainMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 前端菜单相关业务处理服务
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@RequiredArgsConstructor
@Service
public class MenuService {
    private final MainMenuRepository menuRepository;
    private final MenuMapper menuMapper;

    /**
     * 获取全部前端菜单项，并确保菜单项顺序。
     *
     * @return 携带有前端菜单项的流
     */
    @Cacheable("Menu;1800")
    public Set<TMenu> fetchAllMenus() {
       // Iterable<MainMenu> menus= menuRepository.findAll();
        Set<TMenu> menus=  StreamSupport.stream(menuRepository.findAll().spliterator(), false)
                .sorted((a, b) -> a.getSort() - b.getSort())
                .map(menuMapper::toDTO)
                .collect(Collectors.toSet());
        menus.forEach(tMenu -> {
            if(tMenu.getScenes()==null ){
                Set<TScene> tScenes = new HashSet<>();
                tMenu.getChildren().forEach(tMenu1 -> {
                    tScenes.addAll(tMenu1.getScenes());
                });
                tMenu.setScenes(tScenes);
            }
        });
        return menus;
    }

}
