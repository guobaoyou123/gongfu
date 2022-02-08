package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * 供应商公司信息加品牌
 *
 * @author zgh
 * @create_at 2021-12-27
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comp_base")
public class Suppliers implements Serializable {

    /**
     * 公司编号
     * 公司编号在每个公司中有效，同一公司可以有不同的多个编号
     * 入格的公司，其公司编号与id字段相同
     */
    @Id
    @Column(name = "code", length = 20, nullable = false)
    @NotNull
    @NotBlank
    private String code;
    /**
     * 公司简称
     */
    @Column(name = "chi_short", length = 20)
    @NotNull
    @NotBlank
    private String shortNameInCN;

    /**
     * 经营品牌
     */
    @Singular
    @ManyToMany
    @JoinTable(name="comp_allowed_brand",joinColumns = {
        @JoinColumn(name="comp_code",referencedColumnName = "code", insertable = false, updatable = false)
    },inverseJoinColumns = {
        @JoinColumn(name="brand_code",referencedColumnName = "code",insertable = false, updatable = false)
    })
    private Set<BaseBrand> manageBrands;
    /**
     * 自营品牌
     */
    @Singular
    @ManyToMany
    @JoinTable(name="comp_brand_owner",joinColumns = {
        @JoinColumn(name="owner_code",referencedColumnName = "code", insertable = true, updatable = true)
    },inverseJoinColumns = {
        @JoinColumn(name="brand_code",referencedColumnName = "code",insertable = true, updatable = true)
    })
    private Set<BaseBrand> selfSupportBrands;
}
