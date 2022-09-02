package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用于接前端修改采购计划中的需求的请求参数
 */
@Jacksonized
@Data
@NoArgsConstructor
public class VPlanDemandRequest implements Serializable {
    /**
     * 计划编码
     */
    private String planCode;

    /**
     * 产品编码
     */
    private String productId;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 需求数量
     */
    private BigDecimal demand;

}
