package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用于转移合同明细信息
 *
 * @author zgh
 * @create_at 2022-05-26
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TContractRecord {
    /**
     * 序号
     */
    private Integer itemNo;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 产品id
     */
    private String id;

    /**
     * 产品编码
     */
    private String code;

    /**
     * 客户自定义产品编码
     */
    private String customerCustomCode;

    /**
     * 本单位自定义产品编码
     */
    private String compCustomCode;

    /**
     * 描述
     */
    private String describe;

    /**
     * 品牌编码
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 数量
     */
    private BigDecimal amount;

    /**
     * 上一版数量
     */
    private BigDecimal previousAmount;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 含税价格
     */
    private BigDecimal priceVat;

    /**
     * 未税总价
     */
    private BigDecimal totalPrice;

    /**
     * 含税总价
     */
    private BigDecimal totalPriceVat;

    /**
     * 折扣
     */
    private BigDecimal discount;

    /**
     * 上一版未税价格
     */
    private BigDecimal previousPrice;

    /**
     * 上一版未税小计
     */
    private BigDecimal previousTotalPrice;

    /**
     * 上一版含税价格
     */
    private BigDecimal previousPriceVat;

    /**
     * 上一版含税小计
     */
    private BigDecimal previousTotalPriceVat;

    /**
     * 税率
     */
    private BigDecimal vatRate;

    /**
     * 上一版税率
     */
    private BigDecimal previousVatRate;

    /**
     * 备货期
     */
    private int stockTime;

    /**
     * 计价单位
     */
    private String chargeUnit;

    /**
     * 系统计价单位
     */
    private String sysChargeUnit;

    /**
     * 上一版计价单位
     */
    private String previousChargeUnit;

    /**
     * 类型（1-货物 2-服务）
     */
    private String type;

    /**
     * 面价
     */
    private BigDecimal facePrice;

    /**
     * 本单位正在途
     */
    private BigDecimal tranNum;

    /**
     * 本单位正可销
     */
    private BigDecimal deliverNum;

    /**
     * 供应商正可销
     */
    private BigDecimal supplierDeliverNum;

    /**
     * 供应商正在途
     */
    private BigDecimal supplierTranNum;
}
