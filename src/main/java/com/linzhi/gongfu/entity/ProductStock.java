package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_stock")
public class ProductStock {

    @EmbeddedId
    private ProductStockId productStockId;

    /**
     * 实际库存
     */
    @Column(name = "phy_stock")
    private BigDecimal physicalStock;

    /**
     * 未出库数量
     */
    @Column(name = "not_out_stock")
    private BigDecimal notOutStock;

    /**
     * 未入库数量
     */
    @Column(name = "not_instorage_stock")
    private BigDecimal notInStorageStock;
}


