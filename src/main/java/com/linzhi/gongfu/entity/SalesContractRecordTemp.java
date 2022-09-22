package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.VatRateType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 合同记录临时记录表
 */
@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sales_contract_record_temp")
public class SalesContractRecordTemp implements Serializable {
    /**
     * 合同唯一id
     */
    @EmbeddedId
    private SalesContractRecordTempId salesContractRecordTempId;

    /**
     * 父级条目号
     */
    @Column(name = "parent_code")
    private Integer parentCode;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 类型（1-货物 2-服务）
     */
    @Column(length = 1)
    private VatRateType type;

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
     * 规格
     */
    @Column(name = "specification", length = 10)
    private BigDecimal specification;

    /**
     * 系统计价单位
     */
    @Column(name = "sys_charge_unit")
    private String sysChargeUnit;

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
     * 面价
     */
    @Column(name = "face_price")
    private BigDecimal facePrice;

    /**
     * 税率
     */
    @Column(name = "vat_rate")
    private BigDecimal vatRate;

    /**
     * 税率
     */
    @Column(name = "pre_vat_rate")
    private BigDecimal previousVatRate;

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
     * 系统数量
     */
    @Column(name = "sys_quantity")
    private BigDecimal sysAmount;

    /**
     * 数量
     */
    @Column(name = "quantity")
    private BigDecimal amount;


    /**
     * 上一版我的计价单位
     */
    @Column(name = "pre_charge_unit")
    private String previousChargeUnit;

    /**
     * 上一版价格
     */
    @Column(name = "pre_price")
    private BigDecimal previousPrice;

    /**
     * 上一版含税价格
     */
    @Column(name = "pre_price_vat")
    private BigDecimal previousPriceVat;

    /**
     * 上一版数量
     */
    @Column(name = "pre_quantity")
    private BigDecimal previousAmount;


    /**
     * 未税总价
     */
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    /**
     * 上一版未税总价
     */
    @Column(name = "pre_total_price")
    private BigDecimal totalPreviousPrice;

    /**
     * 含税总价
     */
    @Column(name = "total_price_vat")
    private BigDecimal totalPriceVat;

    /**
     * 上一版含税总价
     */
    @Column(name = "pre_total_price_vat")
    private BigDecimal totalPreviousPriceVat;

    /**
     * 上一版服务备注
     */
    @Column(name = "pre_remark")
    private String previousRemark;

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
