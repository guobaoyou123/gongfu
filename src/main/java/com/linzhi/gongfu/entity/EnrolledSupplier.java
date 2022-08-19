package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.TaxMode;
import lombok.*;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 入格供应商实体
 *
 * @author zgh
 * @create_at 2022-08-19
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comp_trade")
public class EnrolledSupplier implements Serializable {

    /**
     * 主键：卖方编号+买方编号
     */
    @EmbeddedId
    private CompTradId compTradId;

    /**
     * 税模式（0：不含税，1：含税）
     */
    @Column(name = "tax_model")
    private TaxMode taxModel;

    /**
     * 状态（0不可交易，2申请交易，1可交易）
     */
    @Column(name = "state")
    private Availability state;

    /**
     * 供应商信息
     */
    @OneToOne
    @JoinColumn(name = "comp_saler", referencedColumnName = "id", insertable = false, updatable = false)
    private  EnrolledCompany  company;

    /**
     * 交易品牌
     */
    @ManyToMany
    @JoinTable(name="comp_trade_brand",joinColumns = {
        @JoinColumn(name="comp_saler",referencedColumnName = "comp_saler", insertable = false, updatable = false),
        @JoinColumn(name="comp_buyer",referencedColumnName = "comp_buyer", insertable = false, updatable = false)
    },inverseJoinColumns = {
        @JoinColumn(name="brand_code",referencedColumnName = "code",insertable = false, updatable = false)
    })
    private List<DcBrand> brands;

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
