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
    private String code;
    private String name;
    private String parent;
    private String location;
    private Integer sort;
    private Set<TScene> scenes;
    private Set<TMenu> children;
}
