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
@Table(name = "product_stock_sum")
public class ProductStockSum {

    @EmbeddedId
    private ProductStockSumId productStockSumId;

    /**
     * 实际库存
     */
    @Column(name = "phy_stock")
    private BigDecimal physicalStock;

    /**
     * 总可销
     */
    @Column(name = "deliver_stock")
    private BigDecimal deliverStock;

    /**
     * 总在途
     */
    @Column(name = "tran_stock")
    private BigDecimal tranStock;

    /**
     * 产品详情
     */
    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    private  Product product;

    /**
     * 安全库存
     */
    @OneToOne
    @JoinColumns({
        @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false),
        @JoinColumn(name = "comp_id", referencedColumnName = "comp_id", insertable = false, updatable = false)
    })
   private  ProductSafetyStock safetyStock;

    /**
     * 单库库存列表
     */
    @OneToMany
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false)
    List<ProductStock> productStocks;
}


