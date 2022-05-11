package com.linzhi.gongfu.dto;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TTemporaryPlan implements Serializable {
    private String dcCompId;
    private String createdBy;
    private String productId;
    private String productCode;
    /**
     * 品牌代码
     */
    private String brandCode;
    /**
     *品牌
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
