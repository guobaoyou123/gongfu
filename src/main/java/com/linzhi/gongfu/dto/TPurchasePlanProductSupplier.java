package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class TPurchasePlanProductSupplier implements Serializable {
    /**
     * 排序
     */
    private int serial;

    /**
     * 供应商公司编码
     */
    private String code;

    /**
     * 供应商公司名称
     */
    private String name;

    /**
     * 可销库存
     */
    private BigDecimal deliverNum;

    /**
     * 在途库存
     */
    private BigDecimal tranNum;

    /**
     * 需求数量
     */
    private BigDecimal demand;
}
