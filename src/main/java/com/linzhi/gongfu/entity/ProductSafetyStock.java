package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_safety_stock")
public class ProductSafetyStock {

    @EmbeddedId
    private ProductSafetyStockId productSafetyStockId;

    /**
     * 安全库存
     */
    @Column(name = "safety_stock")
    private BigDecimal safetyStock;

    /**
     * 单次采购量
     */
    @Column(name = "single_purchase_quantity")
    private BigDecimal singlePurchaseQuantity;


}


