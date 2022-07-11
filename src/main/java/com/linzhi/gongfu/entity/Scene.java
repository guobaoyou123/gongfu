package com.linzhi.gongfu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.linzhi.gongfu.enumeration.CompanyRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * 入格企业类型可用功能场景对照表
 * 用于记录每一种入格公司类型中操作员可以执行的功能，相当于操作员可拥有权限对照表
 *
 * @author xutao
 * @create_at 2022-01-14
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dc_scene")
public class Scene {
    /**
     * 场景编号
     */
    @Id
    @Column(name = "code", length = 6, nullable = false)
    @NonNull
    private String code;

    /**
     * 入格公司角色
     */
    @Column(name = "comp_role", length = 2, nullable = false)
    @NonNull
    private CompanyRole role;

    /**
     * 场景名称
     */
    @Column(length = 20, nullable = false)
    @NonNull
    private String name;

    /**
     * 建议授权描述
     */
    @Column(name = "auth_suggest", length = 50)
    private String authorizationSuggestion;
}
