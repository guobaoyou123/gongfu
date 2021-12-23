package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Enrollment;
import com.linzhi.gongfu.enumeration.TaxModel;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 公司基本信息实体
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
public class DCompany implements Serializable {
    /**
     * 公司编号
     * 公司编号在每个公司中有效，同一公司可以有不同的多个编号
     */
    @Id
    @Column(name = "code", length = 20, nullable = false)
    @NonNull
    private String code;

    /**
     * 外部客户单位编码
     */
    @Column(length = 10)
    private String encode;

    /**
     * 单位类型
     * 可取值：一级、二级、云仓库管理
     */
    @Column(name = "type", length = 40)
    private String type;

    /**
     * 单位类型编号
     */
    @Column(name = "type_code", length = 40)
    private String typeCode;

    /**
     * 入格标志
     */
    @Column
    private Enrollment flag;

    /**
     * 中文名称
     */
    @Column(name = "chi_name", length = 100)
    private String nameInChinese;

    /**
     * 公司地址
     */
    @Column(length = 256)
    private String address;

    /**
     * 地址邮编
     */
    @Column(length = 20)
    private String postCode;

    /**
     * 公司启停用状态
     */
    @Column(length = 1)
    private Availability state;

    /**
     * 公司简称
     */
    @Column(name = "chi_short", length = 20)
    private String shortNameInChinese;

    /**
     * 公司英文全称
     */
    @Column(name = "eng_name", length = 100)
    private String nameInEnglish;

    /**
     * 公司英文简称
     */
    @Column(name = "eng_short", length = 40)
    private String shortNameInEnglish;

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
    @Column(name = "Date_of_esta")
    private LocalDate establishedAt;

    /**
     * 公司经营期限
     */
    @Column(name = "Term_of_business")
    private LocalDate lifetimeUntil;

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
    @Column(name = "Credit_Code", length = 40)
    private String USCI;

    /**
     * 区域编号，区级行政区划编号
     */
    @Column(length = 10)
    private String areaCode;

    /**
     * 区域名称，区级行政区划全称
     */
    @Column(length = 60)
    private String areaName;

    /**
     * 公司使用的二级域名，仅二级域名部分
     */
    @Column(name = "domian_name", length = 50)
    private String subdomainName;

    /**
     * 公司企业电子邮件地址
     */
    @Column(length = 40)
    private String email;

    /**
     * 公司联系人
     */
    @Column(name = "Cont_name", length = 40)
    private String contactName;

    /**
     * 公司联系人电话
     */
    @Column(name = "Cont_phone", length = 20)
    private String contactPhone;

    /**
     * 九宫格平台唯一码
     */
    @Column(name = "id", length = 40)
    private String identityCode;

    /**
     * 税模式
     */
    @Column(length = 1)
    private TaxModel taxModel;

    /**
     * 对应廪实平台客户编号
     */
    @Column(name = "ls_code", length = 20)
    private String lsCode;
}
