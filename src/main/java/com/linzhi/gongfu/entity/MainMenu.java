package com.linzhi.gongfu.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Set;

/**
 * 一级主菜单信息实体
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_menu")
@Where(clause = "parent = '0'")
public class MainMenu extends BaseMenu {

    /**
     * 菜单类型 1-交易 2-仓储
     */
    @Column(name = "type")
    private String type;

    /**
     * 访问菜单所需要的场景（权限）
     */
    @ManyToMany
    @JoinTable(name = "dc_scene_menu", joinColumns = {
        @JoinColumn(name = "menu_code", referencedColumnName = "code", insertable = false, updatable = false)
    }, inverseJoinColumns = {
        @JoinColumn(name = "scene_code", referencedColumnName = "code", insertable = false, updatable = false)
    })
    private Set<Scene> scenes;

    /**
     * 菜单项下所拥有的二级菜单
     */
    @OneToMany
    @JoinColumn(name = "parent", referencedColumnName = "code")
    @OrderBy("sort ASC")
    private Set<SubMenu> children;
}
