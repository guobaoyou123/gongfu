package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.VatRateType;
import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="inquiry_record")
public class InquiryRecord {
    /**
     * 询价单唯一id
     */
   @EmbeddedId
    private InquiryRecordId inquiryRecordId ;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 产品id
     */
    @Column(name = "product_id",length = 64)
    private String productId;
    /**
     * 产品编码
     */
    @Column(name = "product_code",length = 20)
    private String productCode;
    /**
     * 描述
     */
    @Column(name = "product_description",length = 100)
    private String productDescription;

    /**
     * 类型（1-货物 2-服务）
     */
    @Column(length = 1)
    private VatRateType type;
    /**
     * 品牌编码
     */
    @Column(name = "brand_code",length = 10)
    private String brandCode;
    /**
     *品牌名称
     */
    @Column
    private String brand;
    /**
     * 税率
     */
    @Column(name = "vat_rate")
    private BigDecimal vatRate;
    /**
     *计价单位
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
     * 数量
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
     *折扣
     */
    @Column(name = "discount")
    private BigDecimal discount;

    /**
     *折扣后未税价格
     */
    @Column(name = "discounted_price")
    private BigDecimal discountedPrice;
    /**
     *折扣后未税小计
     */
    @Column(name = "total_discounted_price")
    private BigDecimal totalDiscountedPrice;
    /**
     *折扣后含税价格
     */
    @Column(name = "discounted_price_vat")
    private BigDecimal discountedPriceVat;
    /**
     *折扣后含税小计
     */
    @Column(name = "total_discounted_price_vat")
    private BigDecimal totalDiscountedPriceVat;
    /**
     * 备货期
     */
    @Column(name = "stock_time")
    private int stockTime;
}
