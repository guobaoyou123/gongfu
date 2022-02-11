package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Jacksonized
@NoArgsConstructor
public class VTemporaryPlanRequest implements Serializable {
   List<VProduct> products;
    @Data
   public static  class VProduct{
       private String id ;
       private BigDecimal demand;
   }

}
