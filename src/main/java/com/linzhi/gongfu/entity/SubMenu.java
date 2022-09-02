package com.linzhi.gongfu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Set;

/**
 * 二级主菜单信息实体
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "sys_menu")
@Where(clause = "parent != '0'")
public class SubMenu extends BaseMenu {
    @ManyToMany
    @JoinTable(name = "dc_scene_menu", joinColumns = {
        @JoinColumn(name = "menu_code", referencedColumnName = "code", insertable = false, updatable = false)
    }, inverseJoinColumns = {
        @JoinColumn(name = "scene_code", referencedColumnName = "code", insertable = false, updatable = false)
    })
    private Set<Scene> scenes;
}
