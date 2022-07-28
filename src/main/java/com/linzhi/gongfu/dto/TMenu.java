package com.linzhi.gongfu.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于转移从数据库获取的前端菜单结构
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TMenu {

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 父级id
     */
    private String parent;

    /**
     * url
     */
    private String location;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 场景列表
     */
    private Set<TScene> scenes;

    /**
     *子菜单列表
     */
    private Set<TMenu> children;
}
