package com.linzhi.gongfu.entity;

import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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
     * 访问菜单所需要的场景（权限）
     */
    @OneToOne
    @JoinColumn(name = "scene_code", referencedColumnName = "code", insertable = true, updatable = true)
    private Scene scene;

    /**
     * 菜单项下所拥有的二级菜单
     */
    @OneToMany
    @JoinColumn(name = "parent", referencedColumnName = "code")
    @OrderBy("sort ASC")
    private Set<SubMenu> children;
}
