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

    private String planCode;
    private String productId;
    private String oldSupplierCode;
    private String newSupplierCode;
}
