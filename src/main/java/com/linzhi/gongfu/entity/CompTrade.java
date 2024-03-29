package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.TaxMode;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
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
public class CompTrade implements Serializable {
    /**
     * 主键：卖方编号+买方编号
     */
    @EmbeddedId
    private CompTradeId compTradeId;

    /**
     * 税模式（0：不含税，1：含税）
     */
    @Column(name = "tax_model")
    private TaxMode taxModel;

    /**
     * 状态（0禁用，1启用）
     */
    @Column(name = "state")
    private Availability state;

    /**
     * 供应商
     */
    @OneToOne
    @JoinColumn(name = "comp_saler", referencedColumnName = "code", insertable = false, updatable = false)
    private Company salerCompanys;


    /**
     * 客户
     */
    @OneToOne
    @JoinColumn(name = "comp_buyer", referencedColumnName = "code", insertable = false, updatable = false)
    private Company buyerCompanys;

    /**
     * 可见经营品牌
     */
    @Singular
    @ManyToMany
    @JoinTable(name = "comp_trade_brand", joinColumns = {
        @JoinColumn(name = "comp_saler", referencedColumnName = "comp_saler", insertable = false, updatable = false),
        @JoinColumn(name = "comp_buyer", referencedColumnName = "comp_buyer", insertable = false, updatable = false)
    }, inverseJoinColumns = {
        @JoinColumn(name = "brand_code", referencedColumnName = "code", insertable = false, updatable = false)
    })
    private List<DcBrand> manageBrands;

    /**
     * 授权品牌
     */
    @Singular
    @ManyToMany
    @JoinTable(name = "comp_brand_auth", joinColumns = {
        @JoinColumn(name = "be_auth_comp", referencedColumnName = "comp_saler", insertable = false, updatable = false),
    }, inverseJoinColumns = {
        @JoinColumn(name = "brand_code", referencedColumnName = "code", insertable = false, updatable = false)
    })
    private List<DcBrand> authBrands;

    /**
     * 自营品牌
     */
    @Singular
    @ManyToMany
    @JoinTable(name = "comp_brand_owner", joinColumns = {
        @JoinColumn(name = "owner_code", referencedColumnName = "comp_saler", insertable = false, updatable = false)
    }, inverseJoinColumns = {
        @JoinColumn(name = "brand_code", referencedColumnName = "code", insertable = false, updatable = false)
    })
    private List<DcBrand> selfSupportBrands;

    /**
     * 卖方所属操作员
     */
    @Column(name = "saler_belong_to")
    private String salerBelongTo;

    /**
     * 买方所属操作员
     */
    @Column(name = "buyer_belong_to")
    private String buyerBelongTo;

}
