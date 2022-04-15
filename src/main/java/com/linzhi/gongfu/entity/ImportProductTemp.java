package com.linzhi.gongfu.entity;

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
@Table(name="improt_product_temp")
public class ImportProductTemp implements Serializable {
    @EmbeddedId
    private ImportProductTempId importProductTempId;
    /**
     * 产品id
     */
    @Column(name = "productId",length = 64)
    private String productId;
    /**
     * 产品编码
     */
    @Column(name = "code",length = 20)
    private String code;
    /**
     * 品牌编码
     */
    @Column(name = "brandCode",length = 20)
    private String brandCode;
    /**
     * 品牌名称
     */
    @Column(name = "brandName",length = 20)
    private String brandName;
    /**
     * 价格
     */
    @Column(name = "price",length = 20)
    private String price;
    /*
     *数量
     */
    @Column(name = "amount",length = 20)
    private String amount;

}
