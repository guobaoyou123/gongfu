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
    private String planCode;
    private List<String> products;
    private List<String> suppliers;
}
