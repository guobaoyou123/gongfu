package com.linzhi.gongfu.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Whether;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

/**
 * 公司操作员实体
 *
 * @author xutao
 * @create_at 2021-12-15
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "comp_operator")
public class Operator {
    /**
     * 操作员编号
     */
    @EmbeddedId
    private OperatorId identity;

    /**
     * 关联公司基本信息
     */
    @OneToOne
    @JoinColumn(name = "dc_comp_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonBackReference
    private EnrolledCompany company;

    /**
     * 操作员名称
     */
    @Column(length = 100, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String name;

    /**
     * 操作员登录密码
     */
    @Column(name = "login_password", length = 100, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String password;

    /**
     * 操作员状态
     * 1：启用，0：停用
     */
    @Column(length = 1)
    private Availability state;

    /**
     * 联系方式
     */
    @Column(length = 20)
    private String phone;

    /**
     * 是否为管理员
     */
    @Column(name = "is_admin", length = 1)
    private Whether admin;

    /**
     * 对应廪实平台客户编号
     */
    @Column(name = "ls_code", length = 20)
    private String LSCode;

    /**
     * 操作员所拥有的场景（权限）
     */
    @Singular
    @ManyToMany
    @JoinTable(name = "dc_operator_scene", joinColumns = {
            @JoinColumn(name = "dc_comp_id", referencedColumnName = "dc_comp_id", insertable = true, updatable = true),
            @JoinColumn(name = "code", referencedColumnName = "operator_code", insertable = true, updatable = true)
    }, inverseJoinColumns = {
            @JoinColumn(name = "code", referencedColumnName = "scene_code", insertable = true, updatable = true)
    })
    private Set<Scene> scenes;
}
