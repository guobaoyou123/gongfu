package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.TaxModel;
import com.linzhi.gongfu.enumeration.Trade;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * 交易表
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
@Table(name = "comp_trade")
public class CompTrad implements Serializable {
    /**
     * 主键：卖方编号+买方编号
     */
    @EmbeddedId
    private CompTradId compTradId;
    /**
     * 税模式（0：不含税，1：含税）
     */
    @Column(name = "tax_model")
    private TaxModel taxModel;
    /**
     * 状态（0不可交易，2申请交易，1可交易）
     */
    @Column(name = "state")
    private Trade state;

    /**
     * 供应商
     */
    @OneToOne
    @JoinColumn(name = "comp_saler", referencedColumnName = "code", insertable = false, updatable = false)
    private  Company  companys;


    /**
     * 可见经营品牌
     */
    @Singular
    @ManyToMany
    @JoinTable(name="comp_trade_brand",joinColumns = {
        @JoinColumn(name="comp_saler",referencedColumnName = "comp_saler", insertable = false, updatable = false),
        @JoinColumn(name="comp_buyer",referencedColumnName = "comp_buyer", insertable = false, updatable = false)
    },inverseJoinColumns = {
        @JoinColumn(name="brand_code",referencedColumnName = "code",insertable = false, updatable = false)
    })
    private Set<DcBrand> manageBrands;

    /**
     * 授权品牌
     */
    @Singular
    @ManyToMany
    @JoinTable(name="comp_brand_auth",joinColumns = {
        @JoinColumn(name="be_auth_comp",referencedColumnName = "comp_saler", insertable = false, updatable = false),
   },inverseJoinColumns = {
        @JoinColumn(name="brand_code",referencedColumnName = "code",insertable = false, updatable = false)
    })
    private Set<DcBrand> authBrands;
    /**
     * 自营品牌
     */
    @Singular
    @ManyToMany
    @JoinTable(name="comp_brand_owner",joinColumns = {
        @JoinColumn(name="owner_code",referencedColumnName = "comp_saler", insertable = false, updatable = false)
    },inverseJoinColumns = {
        @JoinColumn(name="brand_code",referencedColumnName = "code",insertable = false, updatable = false)
    })
    private Set<DcBrand> selfSupportBrands;



}