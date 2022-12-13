package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
public class TProductStock {

    /**
     * 仓库编码
     */
    private String code;

    /**
     * 仓库名称
     */
    private String name;

    /**
     * 实际库存
     */
    private BigDecimal physicalStock;

    /**
     * 总可销
     */
    private BigDecimal deliverStock;



}
