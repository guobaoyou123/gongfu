package com.linzhi.gongfu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.linzhi.gongfu.enumeration.Enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private String nameInChinese;

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
     * 对应的公司基本信息，这里利用入格供应商的id与公司信息的code相同的原则进行一对一匹配
     */
    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "code")
    private Company details;
}
