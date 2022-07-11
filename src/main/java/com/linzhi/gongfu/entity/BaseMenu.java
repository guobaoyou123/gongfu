package com.linzhi.gongfu.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 用于定义菜单的基础字段信息类，该类不可实例化，不对应任何数据表
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@MappedSuperclass
public abstract class BaseMenu {
    /**
     * 菜单编号，用于子菜单归属的识别
     */
    @Id
    @Column(length = 6, nullable = false)
    @NonNull
    private String code;

    /**
     * 菜单显示名称
     */
    @Column(length = 100, nullable = false)
    @NonNull
    private String name;

    /**
     * 父级菜单的编号
     */
    @Column(length = 6)
    private String parent;


    /**
     * 菜单在界面上的显示排序
     */
    @Column
    private Integer sort;

    /**
     * 记录创建时间
     */
    @Column(name = "created_at", nullable = true, updatable = false)
    @CreatedDate
    private LocalDateTime createAt;

    /**
     * 记录最后修改时间
     */
    @Column(name = "modifide_at", nullable = true, updatable = true)
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    /**
     * 菜单的描述
     */
    @Column(length = 50)
    private String description;



    /**
     * 菜单英文名称
     */
    @Column(name = "eng_name", length = 100)
    private String nameInEN;

    /**
     * 菜单对应前端路由路径
     */
    @Column(length = 50)
    private String location;
}
