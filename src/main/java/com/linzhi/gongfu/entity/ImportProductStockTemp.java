package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 导入产品库存明细临时表
 */
@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "import_product_stock_temp")
public class ImportProductStockTemp implements Serializable {
    @EmbeddedId
    private ImportProductStockTempId importProductStockTempId;

    /**
     * 产品id
     */
    @Column(name = "productId", length = 64)
    private String productId;

    /**
     * 产品编码
     */
    @Column(name = "code", length = 20)
    private String code;

    /**
     * 品牌编码
     */
    @Column(name = "brand_code", length = 20)
    private String brandCode;

    /**
     * 品牌名称
     */
    @Column(name = "brand_name", length = 20)
    private String brandName;

    /*
     *数量
     */
    @Column(name = "amount", length = 20)
    private String amount;

}
