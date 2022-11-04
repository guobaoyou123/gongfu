package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 我经营的品牌
 *
 * @author zgh
 * @create_time 2022-01-28
 */
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comp_allowed_brand")
public class CompAllowedBrand implements Serializable {
    @EmbeddedId
    private CompAllowedBrandId compAllowedBrandId;

    /**
     * 品牌详情
     */
    @OneToOne
    @JoinColumn(name = "brand_code", referencedColumnName = "code", insertable = false, updatable = false)
    private DcBrand dcBrand;

    /**
     * 优选供应列表
     */
    @OneToMany
    @JoinColumns({
        @JoinColumn(name = "brand_code", referencedColumnName = "brand_code",insertable = false,updatable = false),
        @JoinColumn(name = "dc_comp_code", referencedColumnName = "comp_code",insertable = false,updatable = false)
    })
    private List<PreferenceSupplier>  suppliers;
}
