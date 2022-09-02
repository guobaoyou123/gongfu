package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 供应商对经销商设置可见品牌表
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comp_trade_brand")
public class CompTradBrand implements Serializable {

    @EmbeddedId
    private CompTradBrandId compTradBrandId;

    @Column
    private Integer sort;

    /**
     * 供应商
     */
    @OneToOne
    @JoinColumn(name = "comp_saler", referencedColumnName = "code", insertable = false, updatable = false)
    private Company company;
}
