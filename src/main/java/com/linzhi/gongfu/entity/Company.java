package com.linzhi.gongfu.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.linzhi.gongfu.enumeration.Availability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 公司基本信息实体
 *
 * @author xutao
 * @create_at 2021-12-22
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "comp_base")
public class Company implements Serializable {
    /**
     * 公司编号
     * 公司编号在每个公司中有效，同一公司可以有不同的多个编号
     * 入格的公司，其公司编号与id字段相同
     */
    @Id
    @Column(name = "code", length = 20, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String code;

    /**
     * 外部客户单位编码
     */
    @Column(length = 10)
    private String encode;

    /**
     * 单位角色，表示单位目前在平台上可以执行的任务，值以逗号分割
     * 可取值：[0]平台、[1]经销商、[2]品牌管理方、[3]云仓、[4]预制中心、[5]现场服务、[6]外供商、[7]外客户
     */
    @Column(name = "role", length = 40)
    @NotNull
    @NotBlank
    private String role;

    /**
     * 中文名称
     */
    @Column(name = "chi_name", length = 100)
    @NotNull
    @NotBlank
    private String nameInCN;

    /**
     * 公司地址
     */
    @Column(length = 100)
    private String address;

    /**
     * 地址邮编
     */
    @Column(name = "post_code", length = 20)
    private String postCode;

    /**
     * 公司启停用状态
     */
    @Column(length = 1)
    @NotNull
    private Availability state;

    /**
     * 公司简称
     */
    @Column(name = "chi_short", length = 20)
    @NotNull
    @NotBlank
    private String shortNameInCN;

    /**
     * 公司英文全称
     */
    @Column(name = "eng_name", length = 100)
    private String nameInEN;

    /**
     * 公司英文简称
     */
    @Column(name = "eng_short", length = 40)
    private String shortNameInEN;

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
    @Column(name = "establish_at")
    @Past
    private LocalDate establishedAt;

    /**
     * 公司经营期限
     */
    @Column(name = "expire_at")
    @FutureOrPresent
    private LocalDate expiresAt;

    /**
     * 公司联系电话
     */
    @Column(length = 20)
    private String phone;

    /**
     * 公司传真
     */
    @Column(length = 20)
    private String fax;

    /**
     * 社会统一信用代码（Unified Social Credit Identifier）
     */
    @Column(name = "credit_code", length = 40)
    @NotNull
    @NotBlank
    private String USCI;

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
     * 公司企业电子邮件地址
     */
    @Column(length = 40)
    private String email;

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
     * 九宫格平台唯一码
     */
    @Column(name = "id", length = 40)
    private String identityCode;

    /**
     * 对应廪实平台客户编号
     */
    @Column(name = "ls_code", length = 20)
    private String lsCode;
}
