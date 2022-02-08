package com.linzhi.gongfu.service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.linzhi.gongfu.dto.TMenu;
import com.linzhi.gongfu.mapper.MenuMapper;
import com.linzhi.gongfu.repository.MainMenuRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

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
        return StreamSupport.stream(menuRepository.findAll().spliterator(), false)
                .sorted((a, b) -> a.getSort() - b.getSort())
                .map(menuMapper::toDTO)
                .collect(Collectors.toSet());
    }

}
