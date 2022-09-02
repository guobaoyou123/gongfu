package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TTemporaryPlan implements Serializable {

    /**
     * 公司主键
     */
    private String dcCompId;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 产品id
     */
    private String productId;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 品牌代码
     */
    private String brandCode;

    /**
     * 品牌
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
     * 需求
     */
    private BigDecimal demand;

    /**
     * 创建时间
     */
    private Long createdAt;

    /**
     * 面价
     */
    private BigDecimal facePrice;
}
