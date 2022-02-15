package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "purchase_plan_product_saler")
public class PurchasePlanProductSaler implements Serializable {
    @EmbeddedId
    private PurchasePlanProductSalerId purchasePlanProductSalerId;
    /**
     * 供应商公司名称
     */
    @Column(name = "saler_name",length = 20)
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
