package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用于转移询价单明细信息
 *
 * @author zgh
 * @create_at 2022-04-07
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TInquiryRecord {

    /**
     * 询价单唯一id
     */
    private String inquiryId ;
    /**
     * 序号
     */
    private Integer code ;

    private String createdAt;
    /**
     * 产品id
     */
    private String productId;
    /**
     * 产品编码
     */
    private String productCode;
    /**
     * 描述
     */
    private String productDescription;

    /**
     * 类型（1-货物 2-服务）
     */
    private String type;
    /**
     * 品牌编码
     */
    private String brandCode;
    /**
     *品牌名称
     */
    private String brand;
    /**
     * 税率
     */
    private BigDecimal vatRate;
    /**
     *计价单位
     */
    private String charge_unit;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 含税价格
     */
    private BigDecimal priceVat;
    /**
     * 数量
     */
    private BigDecimal amount;
    /**
     * 未税总价
     */
    private BigDecimal totalPrice;
    /**
     * 含税总价
     */
    private BigDecimal totalPriceVat;

    /**
     *折扣
     */
    private BigDecimal discount;

    /**
     *折扣后未税价格
     */
    private BigDecimal discountedPrice;
    /**
     *折扣后未税小计
     */
    private BigDecimal totalDiscountedPrice;
    /**
     *折扣后含税价格
     */
    private BigDecimal discountedPriceVat;
    /**
     *折扣后含税小计
     */
    private BigDecimal totalDiscountedPriceVat;
    /**
     * 备货期
     */
    private int stockTime;
}
