package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用于转移产品总库存信息
 *
 * @author zgh
 * @create_at 2022-12-13
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TProductStockSum {

    /**
     * 产品唯一码
     */
    private String id;

    /**
     * 产品编码
     */
    private String code;

    /**
     * 品牌编码
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brand;

    /**
     * 产品描述
     */
    private String describe;

    /**
     * 计价单位
     */
    private String chargeUnit;

    /**
     * 实际库存
     */
    private BigDecimal physicalStock;

    /**
     * 总可销
     */
    private BigDecimal deliverStock;

    /**
     * 总在途
     */
    private BigDecimal tranStock;

    /**
     * 安全库存
     */
    private BigDecimal safetyStock;

    /**
     * 单次采购量
     */
    private BigDecimal  singlePurchaseQuantity;

    /**
     * 分库的库存
     */
    List<TProductStock> productStocks;
}
