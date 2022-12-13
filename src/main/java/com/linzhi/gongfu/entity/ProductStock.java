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

    /**
     * 库房详情
     */
    @OneToOne
    @JoinColumn(name = "warehouse_code", referencedColumnName = "code", insertable = false, updatable = false)
    private  WareHouse wareHouse;
}


