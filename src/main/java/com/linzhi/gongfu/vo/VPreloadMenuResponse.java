package com.linzhi.gongfu.vo;

import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于前端预加载功能菜单请求的响应体组建
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VPreloadMenuResponse extends VBaseResponse {
    private Set<VMainMenu> menus;

    /**
     * 用于表示一个前端功能主菜单
     */
    @Data
    public static class VMainMenu {
        /**
         * 菜单编号
         */
        private String code;

        /**
         * 菜单名称
         */
        private String name;

        /**
         * 菜单在界面上的显示顺序
         */
        private Integer sort;

        /**
         * 菜单对应的导航路由路径
         */
        private String location;

        /**
         * 访问菜单对应功能所需要的场景（权限）
         */
        private List<String> scene;

        /**
         * 主菜单所携带的子菜单集合
         */
        private Set<VSubMenu> children;
    }

    /**
     * 用于表示一个前端功能子菜单
     */
    @Data
    public static class VSubMenu {
        /**
         * 菜单编号
         */
        private String code;

        /**
         * 菜单名称
         */
        private String name;

        /**
         * 菜单在界面上的显示顺序
         */
        private Integer sort;

        /**
         * 菜单对应的导航路由路径
         */
        private String location;

        /**
         * 访问菜单对应的功能所需要的场景（权限）
         */
        private List<String> scene;
    }
}
