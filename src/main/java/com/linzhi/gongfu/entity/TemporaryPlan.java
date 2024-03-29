package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购临时计划实体
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchase_plan_temp")
public class TemporaryPlan implements Serializable {
    @EmbeddedId
    private TemporaryPlanId temporaryPlanId;
    /**
     * 产品代码
     */
    @Column(name = "product_code", length = 20, nullable = false)
    private String productCode;
    /**
     * 品牌代码
     */
    @Column(name = "brand_code", length = 10)
    private String brandCode;
    /**
     * 品牌
     */
    @Column(name = "brand", length = 10)
    private String brand;
    /**
     * 产品描述
     */
    @Column(length = 100)
    private String describe;
    /**
     * 计价单位
     */
    @Column(name = "charge_unit", length = 10)
    private String chargeUnit;
    /**
     * 需求
     */
    @Column
    private BigDecimal demand;
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    /**
     * 面价
     */
    @Column(name = "face_price")
    private BigDecimal facePrice;
/*    @OneToOne
    @JoinColumn(name ="product_id",referencedColumnName = "id", insertable = false, updatable = false)
    private Product product;*/
}
