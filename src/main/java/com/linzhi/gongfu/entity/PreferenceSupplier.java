package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 优选供应商
 *
 * @author zgh
 * @create_time 2022-11-04
 */
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comp_preference_saler")
public class PreferenceSupplier implements Serializable {
    @EmbeddedId
    private PreferenceSupplierId preferenceSupplierId;

    /**
     * 排序
     */
    @Column
    private int sort ;

    @OneToOne
    @JoinColumn(name = "comp_saler", referencedColumnName = "code", insertable = false, updatable = false)
    private Company company;

    @OneToOne
    @JoinColumn(name = "brand_code", referencedColumnName = "code", insertable = false, updatable = false)
    private DcBrand dcBrand;
}
