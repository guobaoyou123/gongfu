package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Whether;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message_inquiry_record")
public class NotificationInquiryRecord {
    /**
     * 主键
     */
    @EmbeddedId
    private NotificationInquiryRecordId notificationInquiryRecordId;

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
     * 描述
     */
    @Column(name = "product_description", length = 100)
    private String productDescription;

    /**
     * 品牌编码
     */
    @Column(name = "brand_code", length = 10)
    private String brandCode;
    /**
     * 品牌名称
     */
    @Column(name = "brand")
    private String brand;
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
     * 数量
     */
    @Column(name = "quantity")
    private BigDecimal amount;
    /**
     * 是否可以报价
     */
    @Column(name = "is_quote")
    private Whether isQuote;

}
