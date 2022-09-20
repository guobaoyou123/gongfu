package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.VatRateType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 合同记录
 */
@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sales_contract_record_rev")
public class SalesContractRecord {
    /**
     * 合同唯一id
     */
    @EmbeddedId
    private SalesContractRecordId salesContractRecordId;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 所属产品条目号
     */
    @Column(name = "parent_code", nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private Integer parentCode;

    /**
     * 产品id
     */
    @Column(name = "product_id", length = 64)
    private String productId;

    /**
     * 产品编码
     */
    @Column(name = "product_code", length = 20)
    private String productCode;

    /**
     * 客户自定义产品代码
     */
    @Column(name = "customer_custom_code", length = 50)
    private String customerCustomCode;

    /**
     * 本单位自定义产品代码
     */
    @Column(name = "comp_custom_code", length = 50)
    private String compCustomCode;

    /**
     * 描述
     */
    @Column(name = "product_description", length = 100)
    private String productDescription;

    /**
     * 类型（1-货物 2-服务）
     */
    @Column(length = 1)
    private VatRateType type;

    /**
     * 品牌编码
     */
    @Column(name = "brand_code", length = 10)
    private String brandCode;

    /**
     * 品牌名称
     */
    @Column
    private String brand;

    /**
     * 税率
     */
    @Column(name = "vat_rate")
    private BigDecimal vatRate;

    /**
     * 系统计价单位
     */
    @Column(name = "sys_charge_unit")
    private String sysChargeUnit;

    /**
     * 计价单位比例
     */
    @Column(name = "ratio")
    private BigDecimal ratio;

    /**
     * 计价单位
     */
    @Column(name = "charge_unit")
    private String chargeUnit;

    /**
     * 面价
     */
    @Column(name = "face_price")
    private BigDecimal facePrice;

    /**
     * 价格
     */
    @Column(name = "price")
    private BigDecimal price;

    /**
     * 含税价格
     */
    @Column(name = "price_vat")
    private BigDecimal priceVat;

    /**
     * 数量
     */
    @Column(name = "sys_quantity")
    private BigDecimal sysAmount;

    /**
     * 按我的计价单位的数量
     */
    @Column(name = "quantity")
    private BigDecimal amount;

    /**
     * 未税总价
     */
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    /**
     * 含税总价
     */
    @Column(name = "total_price_vat")
    private BigDecimal totalPriceVat;

    /**
     * 折扣
     */
    @Column(name = "discount")
    private BigDecimal discount;

    /**
     * 折扣后未税价格
     */
    @Column(name = "discounted_price")
    private BigDecimal discountedPrice;

    /**
     * 折扣后未税小计
     */
    @Column(name = "total_discounted_price")
    private BigDecimal totalDiscountedPrice;

    /**
     * 折扣后含税价格
     */
    @Column(name = "discounted_price_vat")
    private BigDecimal discountedPriceVat;

    /**
     * 折扣后含税小计
     */
    @Column(name = "total_discounted_price_vat")
    private BigDecimal totalDiscountedPriceVat;

    /**
     * 规格
     */
    @Column(name = "specification")
    private BigDecimal specification;

    /**
     * 服务备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 单独采购数量
     */
    @Column(name = "single_purchase_quantity")
    private String singlePurchaseQuantity;

}
