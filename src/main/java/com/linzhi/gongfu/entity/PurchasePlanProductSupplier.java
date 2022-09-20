package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "purchase_plan_product_saler")
public class PurchasePlanProductSupplier implements Serializable {
    @EmbeddedId
    private PurchasePlanProductSupplierId purchasePlanProductSupplierId;
    /**
     * 序号
     */
    @Column(name = "serial")
    private int serial;

    /**
     * 供应商公司名称
     */
    @Column(name = "saler_name", length = 20)
    private String salerName;

    /**
     * 可销库存
     */
    @Column(name = "deliver_num")
    private BigDecimal deliverNum;

    /**
     * 在途库存
     */
    @Column(name = "tran_num")
    private BigDecimal tranNum;

    /**
     * 需求数量
     */
    @Column(name = "demand")
    private BigDecimal demand;

}
