package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于接生成采购计划请求参数
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VPurchasePlanRequest {
    /**
     * 计划编码
     */
    private String planCode;

    /**
     * 产品编码列表
     */
    private List<String> products;

    /**
     * 供应商编码列表
     */
    private List<String> suppliers;
}
