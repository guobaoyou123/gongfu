package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * 用于接前端更换供应商的请求参数
 */
@Jacksonized
@Data
@NoArgsConstructor
public class VPlanSupplierRequest implements Serializable {
    /**
     * 计划编码
     */
    private String planCode;

    /**
     * 产品编码
     */
    private String productId;

    /**
     * 原供应商编码
     */
    private String oldSupplierCode;

    /**
     * 更换的新的供应商编码
     */
    private String newSupplierCode;
}
