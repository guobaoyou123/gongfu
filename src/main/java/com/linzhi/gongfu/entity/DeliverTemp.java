package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.DeliverType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deliver_temp")
public class DeliverTemp {

    @EmbeddedId
    private DeliverTempId deliverTempId;

    /**
     * 类型（1-发货 2-收货 3-退回 4-收回 5-不退回 6-不收回）
     */
    @Column
    private DeliverType type;

    /**
     * 产品主键
     */
    @Column(name = "product_id", length = 64)
    private String productId;

    /**
     * 产品编码
     */
    @Column(name = "product_code", length = 64)
    private String productCode;

    /**
     * 客户自定义产品代码
     */
    @Column(name = "customer_custom_code", length = 50)
    private String customerPCode;

    /**
     * 本单位自定义产品代码
     */
    @Column(name = "comp_custom_code", length = 50)
    private String localPCode;

    /**
     * 产品描述
     */
    @Column(name = "product_description", length = 100)
    private String productDescription;

    /**
     * 品牌编码
     */
    @Column(name = "brand_code", length = 10)
    private String brandCode;

    /**
     * 税率
     */
    @Column(name = "vat_rate")
    private BigDecimal vatRate;

    /**
     * 品牌
     */
    @Column(length = 20)
    private String brand;

    /**
     * 计价单位
     */
    @Column(name = "charge_unit")
    private String chargeUnit;

    /**
     * 计价单位比例
     */
    @Column
    private BigDecimal ratio;

    /**
     * 系統计价单位
     */
    @Column(name = "sys_charge_unit")
    private String sysChargeUnit;

    /**
     * 未税价格
     */
    @Column
    private BigDecimal price;

    /**
     * 含税价格
     */
    @Column(name = "price_vat")
    private BigDecimal priceVat;

    /**
     * 批检数量
     */
    @Column(name = "quantity")
    private BigDecimal amount;

    /**
     * 系統批检数量
     */
    @Column(name = "sys_quantity")
    private BigDecimal sysAmount;

}
