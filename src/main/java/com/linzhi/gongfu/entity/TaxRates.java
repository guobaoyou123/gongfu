package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.VatRateType;
import com.linzhi.gongfu.enumeration.Whether;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 税率表
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="dc_tax_rates")
public class TaxRates implements Serializable {
    @Id
    @Column
    @NonNull
    @NotBlank
    @NotNull
    private  String id;
    /**
     * 使用国家
     */
    @Column(name = "use_country",length = 3)
    private String useCountry;
    /**
     * 类型(1货物，2服务)
     */
    @Column
    private VatRateType type;
    /**
     * 税率编号
     */
    @Column
    private String code;
    /**
     * 税率
     */
    @Column
    private BigDecimal rate;
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 启用（1是，0否)
     */
    @Column
    private Availability state;

    /**
     * 默认（1-是 0-否）
     */
    @Column
    private Whether deflag;

}
