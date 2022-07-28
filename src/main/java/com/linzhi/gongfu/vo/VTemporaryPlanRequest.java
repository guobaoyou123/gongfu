package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用于接前端保存临时采购计划的请求参数
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VTemporaryPlanRequest implements Serializable {

    /**
     * 产品编码
     */
    private String productId ;

    /**
     * 需求数量
     */
    private BigDecimal demand;


}
