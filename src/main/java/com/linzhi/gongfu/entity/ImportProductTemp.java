package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.TaxMode;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 导入产品明细临时表
 */
@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "improt_product_temp")
public class ImportProductTemp implements Serializable {
    @EmbeddedId
    private ImportProductTempId importProductTempId;

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

    /**
     * 价格
     */
    @Column(name = "price", length = 20)
    private String price;


    /*
     *数量
     */
    @Column(name = "amount", length = 20)
    private String amount;

    /*
     *判断导入的是未税单价还是含税单价（0-未税单价 1-含税单价）
     */
    @Column(length = 1)
    private TaxMode flag;

}
