package com.linzhi.gongfu.vo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VVerificationPlanResponse extends VBaseResponse{

    private List<VProduct> products;
     @Data
    public static class VProduct{
         private String id ;
         private String code;
     }
}
