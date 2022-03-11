package com.linzhi.gongfu.dto;

import com.linzhi.gongfu.enumeration.DemandSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 用于转移采购计划产品基本信息
 *
 * @author zgh
 * @create_at 2022-02-15
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TPurchasePlanProduct implements Serializable {
    private String id;
    /**
     * 产品代码
     */
    private String code;
    /**
     * 品牌编码
     */
    private String brandCode;
    /**
     * 可销库存
     */
    private BigDecimal deliverNum;
    /**
     * 在途库存
     */
    private BigDecimal tranNum;
    /**
     * 需求总数量
     */
    private BigDecimal demand;
    /**
     * 安全库存
     */
    private BigDecimal safetyStock;
    /**
     * 上次采购价格
     */
    private BigDecimal beforeSalesPrice;
    /**
     * 正在询价数量
     */
    private BigDecimal inquiryNum;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 产品描述
     */
    private String describe;
    /**
     * 计价单位
     */
    private String chargeUnit;
    /**
     * 面价
     */
    private BigDecimal facePrice;
    /**
     * 创建时间
     */
    private long createdAt;
    private List<TPurchasePlanProductSupplier> suppliers;
}
