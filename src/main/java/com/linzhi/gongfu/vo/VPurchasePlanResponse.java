package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于响应前端对于购计划的预加载请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VPurchasePlanResponse extends VBaseResponse{
    private VPurchasePlan plan;
    @Data
    public static class VPurchasePlan{

    }
}
