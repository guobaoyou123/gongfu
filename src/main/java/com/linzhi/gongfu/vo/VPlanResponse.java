package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VPurchasePlanResponse extends VBaseResponse{
    private String planCode;
    private VPurchasePlan plan;

    @Data
    public static  class VPurchasePlan{
        private String planCode;
        private 
    }
}
