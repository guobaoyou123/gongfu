package com.linzhi.gongfu.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Whether;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
@Table(name = "comp_user")
public class DOperator {
    /**
     * 操作员编号
     */
    @EmbeddedId
    private DOperatorId identity;

    /**
     * 关联公司基本信息
     */
    @ManyToOne
    @JoinColumn(name = "comp_code")
    @JsonBackReference
    private DCompany company;

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
    @Column(length = 100, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String loginPassword;

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
    @Column(name = "admin_YesNo", length = 1)
    private Whether admin;

    /**
     * 对应廪实平台客户编号
     */
    @Column(name = "ls_code", length = 20)
    private String LSCode;

    /**
     * 操作员使用的语言
     */
    @Column(length = 1)
    private String language;

    /**
     * 操作员最后一次登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginAt;
}
