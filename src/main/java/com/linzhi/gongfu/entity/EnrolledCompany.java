package com.linzhi.gongfu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.linzhi.gongfu.enumeration.Enrollment;

import com.linzhi.gongfu.enumeration.Whether;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 系统入格单位表
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "dc_comp")
public class EnrolledCompany {
    /**
     * 九宫格平台唯一码
     */
    @Id
    @Column(name = "id", nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String id;

    /**
     * 中文名称
     */
    @Column(name = "chi_name", length = 100)
    @NotNull
    @NotBlank
    private String nameInCN;

    /**
     * 社会统一信用代码（Unified Social Credit Identifier）
     */
    @Column(name = "credit_code", length = 40)
    @NotNull
    @NotBlank
    private String USCI;

    /**
     * 公司联系人
     */
    @Column(name = "cont_name", length = 40)
    private String contactName;

    /**
     * 公司联系人电话
     */
    @Column(name = "cont_phone", length = 20)
    private String contactPhone;

    /**
     * 公司使用的二级域名，仅二级域名部分
     */
    @Column(name = "domain", length = 50)
    private String subdomainName;

    /**
     * 入格标志
     */
    @Column
    @NotNull
    private Enrollment state;

    /**
     * 对应的公司基本信息，这里利用入格单位的id与公司信息的code相同的原则进行一对一匹配
     */
    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "code", insertable = true, updatable = true)
    private Company details;

    /**
     * 地址邮编
     */
    @Column(name = "post_code", length = 20)
    private String postCode;

    /**
     * 公司Logo文本
     */
    @Column(length = 100)
    private String logo;

    /**
     * 公司Logo图片所在位置（路径）
     */
    @Column(name = "logobmp", length = 200)
    private String logoPath;

    /**
     * 公司商标文本
     */
    @Column(length = 100)
    private String trademark;

    /**
     * 公司商标图片所在位置（路径）
     */
    @Column(name = "trademarkbmp", length = 200)
    private String trademarkPath;

    /**
     * 公司官网地址
     */
    @Column(name = "Official_website")
    private String website;

    /**
     * 公司成立日期
     */
    @Column(name = "establish_at", columnDefinition = "DATE")
    @Past
    private LocalDate establishedAt;

    /**
     * 公司经营期限
     */
    @Column(name = "expire_at", columnDefinition = "DATE")
    @FutureOrPresent
    private LocalDate expiresAt;

    /**
     * 公司传真
     */
    @Column(length = 20)
    private String fax;

    /**
     * 公司简介
     */
    @Column
    private String  introduction;

    /**
     * 是否对格友可见
     */
    @Column(name = "visible")
    private Whether visible;

    /**
     * 对应的公司设置的对外可见内容，这里利用入格供应商的id与公司信息的code相同的原则进行一对一匹配
     */
    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "dc_comp_id", insertable = true, updatable = true)
    private CompVisible compVisible;
}
