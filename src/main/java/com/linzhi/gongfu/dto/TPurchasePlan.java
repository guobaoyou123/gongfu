package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用于转移采购计划基本信息
 *
 * @author zgh
 * @create_at 2022-02-15
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TPurchasePlan implements Serializable {

    /**
     * 计划编码
     */
    private String planCode;

    /**
     * 对应销售合同号
     */
    private String salesCode;


    /**
     * 计划产品列表
     */
    private List<TPurchasePlanProduct> products;
}
