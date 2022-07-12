package com.linzhi.gongfu.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Whether;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

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
public class OperatorDetail {
    /**
     * 操作员编号
     */
    @EmbeddedId
    private OperatorId identity;

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
     * 出身日期
     */
    @Column(name = "birthday")
    private LocalDate birthday;

    /**
     * 性别
     */
    @Column(name = "sex")
    private String  sex;

    /**
     * 区域编号，区级行政区划编号
     */
    @Column(name = "area_code", length = 6)
    private String areaCode;

    /**
     * 区域名称，区级行政区划全称
     */
    @Column(name = "area_name", length = 60)
    private String areaName;

    /**
     * 详细地址
     */
    @Column(length = 100)
    private String address;

    /**
     * 入职日期
     */
    @Column(name = "entry_at")
    private LocalDate entryAt;

    /**
     * 离职日期
     */
    @Column(name = "resignation_at")
    private LocalDate resignationAt;
}
